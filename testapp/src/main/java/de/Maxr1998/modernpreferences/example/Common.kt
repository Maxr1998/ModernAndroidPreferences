package de.Maxr1998.modernpreferences.example

import android.content.Context
import de.Maxr1998.modernpreferences.helpers.*

object Common {
    fun createRootScreen(context: Context) = screen(context) {
        pref("one") {
            title = "Important"
            description = "It actually is!"
            iconRes = R.drawable.ic_important_24dp
        }
        pref("two") {
            title = "Also important"
        }
        categoryHeader("header") {
            title = "More"
        }
        subScreen {
            title = "Subscreen"
            centerIcon = false
            pref("three") {
                title = "A not so important sub-preference"
                iconRes = R.drawable.ic_emoji_24dp
            }
        }
        switch("four") {
            title = "Switch"
            description = "This is a switch"
        }
        collapse {
            pref("five") {
                title = "Invisible by default"
            }
            pref("six") {
                title = "With icon!"
                iconRes = R.drawable.ic_emoji_24dp
            }
            pref("seven") {
                title = "See summary"
                description = "Here we also have a summary"
            }
        }
    }
}