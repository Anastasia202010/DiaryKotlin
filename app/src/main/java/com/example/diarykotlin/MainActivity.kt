package com.example.diarykotlin

import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.diarykotlin.databinding.ActivityAddDealBinding
import com.example.diarykotlin.databinding.ActivityMainBinding
import io.realm.Realm


var realm: Realm? = null


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var deals = mutableListOf<Deal>()
    private var calendarView: CalendarView? = null
    private var hourHeight = 120.0
    private var selectedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        println("aaa lf")
        Realm.init(this)
        Realm.getDefaultInstance().also { realm = it }

        binding.btnAddDeal.setOnClickListener {
            val intent = Intent(this,AddDealActivity::class.java)
            startActivity(intent)
        }

        calendarView = findViewById(R.id.calendarView)
        calendarView!!.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            update()
        }
        update()
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    private fun intToTime(j: Int): String {
        return if (j < 10) "0$j:00" else "$j:00"
    }

    private fun dealInSelectedDay(d: Deal): Boolean {
        val dealDate: String? = d.date
        val selectedDateStr: String? = DateUtils.formatDateTime(
            this,
            selectedDate.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
        )
        if (dealDate != null) {
            return dealDate == selectedDateStr
        }
        return false
    }

    private fun readData() {
        if (realm != null) {
            println("вот realm хоть есть ")
            if (realm!!.where(Deal::class.java).findAll().size != 0) {
                println("вот сколько записей " + realm!!.where(Deal::class.java).findAll().size)
                deals.clear()
                for (i in realm!!.where(Deal::class.java).findAll()
                    .filter { d -> dealInSelectedDay(d) }) {
                    deals.add(i)
                    println("вот что прочитал " + i.name + " " + i.timeStart + " " + i.timeFinish)
                }
            }
        }
    }


    private fun update() {

        readData()
        val dealsColumns: LinearLayout = findViewById(R.id.deals_columns)
        dealsColumns.removeAllViews()

        val hours = LinearLayout(this)
        val lph = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lph.setMargins(20, 3, 10, 0)
        hours.layoutParams = lph
        hours.orientation = LinearLayout.VERTICAL

        for (i in 0..23) {
            val hour = TextView(this)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 0)
            hour.setBackgroundColor(Color.CYAN)
            hour.layoutParams = lp

            hour.text = " " + intToTime(i)
            hour.setTextColor(Color.BLACK)
            hour.layoutParams.height = hourHeight.toInt()
            hour.layoutParams.width = 120
            hours.addView(hour)
        }
        dealsColumns.addView(hours)

        //for width of deals
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        if (deals.size != 0) {
            val dealWidth = (displayMetrics.widthPixels - 250) / deals.size

            for (i in deals) {

                val dealColumn = LinearLayout(this)
                val lpd = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lpd.setMargins(0, 3, 5, 0)
                dealColumn.layoutParams = lpd
                dealColumn.orientation = LinearLayout.VERTICAL

                val block = Space(this)
                block.setBackgroundColor(Color.MAGENTA)
                block.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                block.layoutParams.height = (hourHeight / 60 * i.timeStart).toInt()
                block.layoutParams.width = dealWidth

                dealColumn.addView(block)

                val deal = TextView(this)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 0, 0, 0)
                deal.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_lite))
                deal.layoutParams = lp
                deal.setPadding(15, 0, 0, 0)
                if (i.description != "")
                    deal.text = i.name + "\n--------\n(" + i.description + ")"
                else
                    deal.text = i.name
                deal.setTextColor(Color.BLACK)
                deal.layoutParams.height = (hourHeight / 60 * (i.timeFinish - i.timeStart)).toInt()
                deal.layoutParams.width = dealWidth
                dealColumn.addView(deal)

                val block2 = Space(this)
                block2.setBackgroundColor(Color.MAGENTA)
                block2.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                block2.layoutParams.height = (hourHeight / 60 * (24 * 60 - i.timeFinish)).toInt()
                block2.layoutParams.width = dealWidth

                dealColumn.addView(block2)
                dealsColumns.addView(dealColumn)

            }
        }

    }
}