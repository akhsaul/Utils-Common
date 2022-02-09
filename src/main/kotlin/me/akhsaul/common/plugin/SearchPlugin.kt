package me.akhsaul.common.plugin

interface SearchPlugin : HostPlugin {
    fun getSearchResult(): List<Map<String, String>>
}