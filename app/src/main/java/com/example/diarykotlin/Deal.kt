package com.example.diarykotlin

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required


open class Deal : RealmObject() {

    @PrimaryKey
    @Required
    var id: String? = null

    var name: String? = null
    var color: String? = null
    var timeStart: Int = 0
    var timeFinish: Int = 0
    var date: String? = null
    var description: String? = null

}

