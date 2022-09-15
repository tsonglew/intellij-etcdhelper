package com.github.tsonglew.etcdhelper.data

class GroupKeyTreeNode(val key: String, val children: ArrayList<String>) {
    private val map = hashMapOf<String, ArrayList<String>>()

    fun group(groupBySymbol: String) {
        children.forEach {
            val groupSymbolIdx = it.indexOf(groupBySymbol)
            if (groupSymbolIdx < 0) {
                map[it] = arrayListOf()
            } else {
                val newKey = it.subSequence(0, groupSymbolIdx).toString()
                if (!map.containsKey(newKey)) {
                    map[newKey] = arrayListOf()
                }
                map[newKey]!!.add(it.substring(groupSymbolIdx, it.length))
            }
        }
    }
}
