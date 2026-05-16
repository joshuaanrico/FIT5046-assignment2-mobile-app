package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * For the runtime state of the live session.
 * Scoped to the session flow to ensure that a single instance is being share,
 * between live session screen and alert screen. - preventing the use of having ot use companion obj.
 */
class LiveSessionViewModel(application: Application) : AndroidViewModel(application){
}