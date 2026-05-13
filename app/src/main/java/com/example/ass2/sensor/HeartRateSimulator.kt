package com.example.ass2.sensor

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * To simulate the heart rate of the app user.
 * Takes the users age, gets the data for that age range,
 * normalizes the max HR values and
*/
class HeartRateSimulator {

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
        val targetHR: Int = (restingHR + 25) .coerceIn(75, 105)

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