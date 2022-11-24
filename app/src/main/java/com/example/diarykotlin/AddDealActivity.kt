package com.example.diarykotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.icu.util.Calendar
import android.text.format.DateUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView

class AddDealActivity : AppCompatActivity() {

    private var editTextName: EditText? = null
    private var editTextDescription: EditText? = null
    private var editTextDate: TextView? = null
    private var editTextTime: TextView? = null
    private var editTextTime2: TextView? = null
    private var textViewError1: TextView? = null
    private var textViewError2: TextView? = null
    private var dateAndTimeStart: Calendar = Calendar.getInstance()
    private var dateAndTimeFinish: Calendar = Calendar.getInstance()
    private var deal:Deal=Deal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deal)
        editTextName = findViewById(R.id.editTextName)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextTime2 = findViewById(R.id.editTextTime2)
        textViewError1 = findViewById(R.id.textViewError1)
        textViewError2 = findViewById(R.id.textViewError2)
        setInitialDate()
        setInitialTimeStart()
        setInitialTimeEnd()

    }

    fun addData(v: View?){
        deal.name= editTextName?.text.toString()
        deal.description= editTextDescription?.text.toString()

        deal.date= DateUtils.formatDateTime(
            this,
            dateAndTimeStart.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR

        )
        deal.timeStart= dateAndTimeStart.get(Calendar.MILLISECONDS_IN_DAY)/1000/60
        deal.timeFinish= dateAndTimeFinish.get(Calendar.MILLISECONDS_IN_DAY)/1000/60

        deal.id = java.util.UUID.randomUUID().toString()
        println("вот это хочу записать "+deal.name+deal.date+" "+deal.id)
        if(deal.timeStart>=deal.timeFinish || deal.name==""){
            if(deal.timeStart>=deal.timeFinish)
                textViewError2?.visibility = View.VISIBLE
            else
                textViewError2?.visibility = View.INVISIBLE
            if(deal.name=="")
                textViewError1?.visibility = View.VISIBLE
            else
                textViewError1?.visibility = View.INVISIBLE
        }
        else{
            try {
                realm!!.beginTransaction()
                realm!!.copyToRealmOrUpdate(deal)
                realm!!.commitTransaction()
                //realm!!.close()
            } catch (e: Exception) {
                println(e)
            }
            finish()
        }

    }


    // Dialogs for date and time
    fun setDate(v: View?) {
        DatePickerDialog(
            this@AddDealActivity, d,
            dateAndTimeStart.get(Calendar.YEAR),
            dateAndTimeStart.get(Calendar.MONTH),
            dateAndTimeStart.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

    fun setTimeStart(v: View?) {
        TimePickerDialog(
            this@AddDealActivity, t1,
            dateAndTimeStart.get(Calendar.HOUR_OF_DAY),
            dateAndTimeStart.get(Calendar.MINUTE), true
        )
            .show()
    }

    fun setTimeEnd(v: View?) {
        TimePickerDialog(
            this@AddDealActivity, t2,
            dateAndTimeFinish.get(Calendar.HOUR_OF_DAY),
            dateAndTimeFinish.get(Calendar.MINUTE), true
        )
            .show()
    }

    // Set initial date and time(current)
    private fun setInitialDate() {
        editTextDate?.text = DateUtils.formatDateTime(
            this,
            dateAndTimeStart.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR

        )
    }

    private fun setInitialTimeStart() {
        editTextTime?.text = DateUtils.formatDateTime(
            this,
            dateAndTimeStart.timeInMillis,
            DateUtils.FORMAT_SHOW_TIME
        )
    }

    private fun setInitialTimeEnd() {
        editTextTime2?.text = DateUtils.formatDateTime(
            this,
            dateAndTimeFinish.timeInMillis,
            DateUtils.FORMAT_SHOW_TIME
        )
    }

    // Listeners
    private var t1 =
        OnTimeSetListener { _, hourOfDay, minute ->
            dateAndTimeStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
            dateAndTimeStart.set(Calendar.MINUTE, minute)
            setInitialTimeStart()
        }

    private var t2 =
        OnTimeSetListener { _, hourOfDay, minute ->
            dateAndTimeFinish.set(Calendar.HOUR_OF_DAY, hourOfDay)
            dateAndTimeFinish.set(Calendar.MINUTE, minute)
            setInitialTimeEnd()
        }

    private var d =
        OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            dateAndTimeStart.set(Calendar.YEAR, year)
            dateAndTimeStart.set(Calendar.MONTH, monthOfYear)
            dateAndTimeStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setInitialDate()
        }



}