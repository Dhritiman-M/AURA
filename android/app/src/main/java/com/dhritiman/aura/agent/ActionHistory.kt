package com.dhritiman.aura.agent


data class ActionRecord(

    val actionName: String,

    val success: Boolean,

    val timestamp: Long =
        System.currentTimeMillis()
)



class ActionHistory {


    private val actions =
        mutableListOf<ActionRecord>()



    fun add(
        actionName: String,
        success: Boolean
    ) {

        actions.add(

            ActionRecord(
                actionName,
                success
            )
        )
    }



    fun getHistory():
            List<ActionRecord> {

        return actions.toList()
    }



    fun lastAction():
            ActionRecord? {

        return actions.lastOrNull()
    }
}