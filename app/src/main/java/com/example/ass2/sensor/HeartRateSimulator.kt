package com.example.ass2.sensor

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * To simulate the heart rate of the app user.
 * Takes the users age, gets the data for that age range,
 * the max HRs for the age range, calculates the threshold value,
 * at which point a warning is due.
 *
 * @author Yapa Jayasinghe
*/
class HeartRateSimulator(private val context: Context) {

    /**
     * Uses the list of Int of the maxHRs for the user age range,
     * to calculate the avg and then calculates the threshold HR value.
     *
     * @param ageOfUser current users age.
     * @return threshold HR for warning screen to be displayed.
     */
    fun getThresholdHRForAge(ageOfUser: Int) : Int
    {
        val avgMaxHR = readMaxHRDataForAgeRng(ageOfUser).average()
        // warning to be given at 75% of the avg maxHR, before It's too late.
        val threshHold = (avgMaxHR * 0.70).toInt()
        // a min of 85 so that the warning won't happen in a slight warm up.
        // a max of 120 so that it's not too far off to never hit.
        return threshHold.coerceIn(85,120)
    }

    /**
     * Reads the data from the heart.csv in assets,
     * age and max HR is being considered.
     * gets sufficient data from the closest age range.
     *
     * @param ageOfUser current users age to get data of the near age range.
     * @return List of Int containing max HR for the specific age range.
     */
    fun readMaxHRDataForAgeRng(ageOfUser: Int): List<Int>
    {
        // contains age, max HR
        val allDataRead = mutableListOf<Pair<Int,Int>>()

        try {
            val reader = context.assets.open("heart.csv").bufferedReader()
            reader.readLine() // to skip the header
            var line = reader.readLine()
            while (line != null)
            {
                val columns = line.split(",")
                if (columns.size > 7)
                {
                    val age = columns[0].trim().toIntOrNull()
                    val maxHR = columns[7].trim().toIntOrNull()
                    if(age != null && maxHR != null)
                    {
                        allDataRead.add(Pair(age, maxHR))
                    }
                }
                line = reader.readLine()
            }
            reader.close()
        }
        catch (e: Exception)
        {
            return emptyList()
        }

        // getting sufficient amount of data, starting from the closest / smaller age range.
        val ranges = listOf(5,10,15)
        for (range in ranges)
        {
            val result = mutableListOf<Int>()
            for (dataPair in allDataRead)
            {
                val age = dataPair.first
                val maxHR = dataPair.second
                if (kotlin.math.abs(age - ageOfUser) <= range)
                {
                    result.add(maxHR)
                }
            }
            // once enough data is found stop and return the result.
            if (result.size>=20)
            {
                return result
            }
        }

        // when not enough data is to be found return all the maxHRs.
        val allMaxHR = mutableListOf<Int>()
        for (dataPair in allDataRead)
        {
            allMaxHR.add(dataPair.second)
        }
        return allMaxHR
    }

    /**
     * Streaming simulated HR for a seated exercise session.
     * The resting HR is being estimated according to the users age.
     * During the first 3 mins of the session, the BPM is increased to a moderate exercise level.
     * Random variations are being added to make realistic fluctuations.
     * Those values are being emitted with a 1 sec interval(limited to a range).
     *
     * @param ageOfUser current users age to get resting HR.
     * @return Flow of simulated HR values every second.
     */
    fun streamHR(ageOfUser: Int): Flow<Int> = flow {
        /* Flow - defines a stream of int that can be emitted async over time
         (fun needs to be called inside a coroutine). */

        // resting HR - decreases a small amount with age and limited (f - float).
        val restingHR: Int = (75 - (ageOfUser - 20) * 0.1f).toInt().coerceIn(60,80)

        // moderate HR for the workout (seated) - expected reach.
        val targetHR: Int = (restingHR + 40).coerceIn(90, 120)

        // the current HR generated (initially it's the resting HR)
        var currentHR: Double = restingHR.toDouble()
        var secondsPassed = 0
        while (true)
        {
            // how far into the 3 mins we've gone (3 mins = 180 sec)
            val asFraction = (secondsPassed/180f).coerceIn(0f,1f)
            // when halfway through the 3 mins - * 0.5 | full way through 3 mins - * 1 = targetHR
            val currentTargetHR = restingHR + (targetHR - restingHR) * asFraction

            // adding variation
            val variation = (Math.random() * 4 - 2)
            // smoothening (reducing the value changes) the HR,
            // keeping 80% of the previous HR, moving only 20% to currentTarget.
            currentHR = 0.8 * currentHR + 0.2 * (currentTargetHR + variation)

            // assuming that seated min HR wouldn't go below 55.
            emit(currentHR.toInt().coerceIn(55, 130))
            delay(1000L)
            secondsPassed++
        }
    }


}