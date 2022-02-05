package me.akhsaul.common

import me.akhsaul.common.exception.ResourceNotFoundException
import me.akhsaul.common.tools.Data
import me.akhsaul.common.tools.Sys
import java.io.File
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Path
import kotlin.reflect.KClass

@Suppress("unused")
class Resource<T : Any>(private val clazz: Class<T>) {
    constructor(clazz: KClass<T>) : this(clazz.java)

    companion object {
        inline operator fun <reified T : Any> invoke() = Resource(T::class.java)

        private val LOG = logger { }
    }

    /**
     * @return List<URL> or Empty List
     * */
    private fun find(fileName: String, limit: Long = Long.MAX_VALUE): List<URL> {
        require(limit in 1..Long.MAX_VALUE)
        var resource = catch(emptyList()){
            buildList<URL> {
                LOG.info("Start find resource.")
                val loader = Sys.getClassLoader()
                val resources = loader.getResources(fileName)
                if (resources.hasMoreElements()) {
                    LOG.trace("Resource found.")
                    var added = 0L
                    for (resource in resources) {
                        if (added == limit) {
                            break
                        }
                        addNonDuplicate(resource)
                        added++
                    }
                } else {
                    LOG.info("Resource not found directly.")
                    val url = notNull(loader.getResource("/"))
                    LOG.info("URL = $url")
                    val con = url.openConnection()
                    if (con is JarURLConnection) {
                        LOG.info("connection is JarURLConnection.")
                        LOG.info("Try find resource in subdirectory inside jar.")
                        LOG.trace("FilePath=$fileName")
                        con.jarFile.stream().filter {
                            // make sure, exclude directory and "*.class" (java file)
                            !it.isDirectory && !it.name.endsWith(".class")
                        }.filter {
                            LOG.trace("StreamPath=${it.name}")
                            // include only matches with filename
                            it.name.endsWith(fileName)
                        }.limit(limit).forEach { entry ->
                            // unordered entry
                            loader.getResource(entry.name)?.let { addNonDuplicate(it) }
                        }
                    }
                }
            }
        }

        // resource not inside jar
        // maybe resource in current directory
        if (resource.isEmpty()) {
            resource = buildList {
                val path = Path.of(".").toAbsolutePath()
                LOG.info("Try find resource in current directory, $path")
                val filePath = Path.of(fileName)
                LOG.trace("FilePath = $filePath")
                Data(path).walk().forEach {
                    if(it.isFile) {
                        if (it.toPath().endsWith(filePath)) {
                            addNonDuplicate(it.toPath().toUri().toURL())
                        } else {
                            LOG.trace("WalkPath=${it.toPath()}")
                        }
                    }
                }
            }
        }

        return resource
    }

    @Suppress("unchecked_cast")
    fun getAll(fileName: String): List<T> {
        val resources = find(fileName)
        return if (resources.isEmpty()) {
            LOG.info("Resource not found. Return empty list.")
            emptyList()
        } else {
            LOG.info("Resource has been found.")
            when {
                clazz.instanceOf(URL::class) -> {
                    return resources as List<T>
                }
                clazz.instanceOf(File::class) -> {
                    return buildList {
                        resources.forEach {
                            add(File(it.toURI()) as T)
                        }
                    }
                }
                clazz.instanceOf(Path::class) -> {
                    return buildList {
                        resources.forEach {
                            add(Path.of(it.toURI()) as T)
                        }
                    }
                }
                clazz.instanceOf(InputStream::class) -> {
                    return buildList {
                        resources.forEach {
                            add(it.openStream().buffered() as T)
                        }
                    }
                }
                else -> {
                    // throw exception if casting not supported
                    throw UnsupportedOperationException("Cast does not support for $clazz")
                }
            }
        }
    }

    @Suppress("unchecked_cast")
    fun get(fileName: String): T {
        val resources = find(fileName, 1)

        return if (resources.isEmpty()) {
            LOG.info("Resource not found. Throw an exception")
            // return null if resources is empty
            throw ResourceNotFoundException("Can't find '$fileName'.")
        } else {
            LOG.info("Resource has been found.")
            when {
                clazz.instanceOf(URL::class) -> {
                    resources.first() as T
                }
                clazz.instanceOf(URI::class) -> {
                    resources.first().toURI() as T
                }
                clazz.instanceOf(File::class) -> {
                    File(resources.first().toURI()) as T
                }
                clazz.instanceOf(Path::class) -> {
                    Path.of(resources.first().toURI()) as T
                }
                clazz.instanceOf(InputStream::class) -> {
                    resources.first().openStream().buffered() as T
                }
                else -> {
                    // throw exception if casting not supported
                    throw UnsupportedOperationException("Cast does not support for $clazz")
                }
            }
        }
    }
}