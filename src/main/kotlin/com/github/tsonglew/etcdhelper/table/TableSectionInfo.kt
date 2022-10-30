package com.github.tsonglew.etcdhelper.table

data class TableSectionInfo(val title: String, val columns: Array<String>, var infoArray: Array<Array<String>>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableSectionInfo

        if (title != other.title) return false
        if (!infoArray.contentDeepEquals(other.infoArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + infoArray.contentDeepHashCode()
        return result
    }
}
