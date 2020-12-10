# ModernAndroidPreferences

![Latest release](https://img.shields.io/github/v/release/Maxr1998/ModernAndroidPreferences)
![Build status](https://img.shields.io/github/workflow/status/Maxr1998/ModernAndroidPreferences/Build%20library%20and%20test%20app)
![License](https://img.shields.io/github/license/Maxr1998/ModernAndroidPreferences)

Android Preferences defined through Kotlin DSL, shown in a RecyclerView.  
_No XML, no awful PreferenceManager, Fragments or styling problems, no more ListView._ :tada:

#### Code example
```Kotlin
// Setup a preference screen
val screen = screen(context) {
    pref("first") {
        title = "A preference"
        summary = "Click me to do stuff"
        click {
            doStuff()
        }
    }
    pref("second") {
        title = "Another one"
        iconRes = R.drawable.preference_icon_24dp
    }
    categoryHeader("more") {
        titleRes = R.string.category_more
    }
    switch("toggle_feature") {
        title = "Also supports switches"
    }
    // and many other preference widgets!
}

// Wrap the created screen in a preference adapter…
val preferencesAdapter = PreferencesAdapter(screen)

// …that can be attached to a RecyclerView
recyclerView.adapter = preferencesAdapter
```

#### View the example app
Example Activities ([with](https://github.com/Maxr1998/ModernAndroidPreferences/tree/master/testapp/src/main/java/de/Maxr1998/modernpreferences/example/view_model) and [without](https://github.com/Maxr1998/ModernAndroidPreferences/blob/master/testapp/src/main/java/de/Maxr1998/modernpreferences/example/TestActivity.kt) using ViewModel)
show advanced info like back handling, saving/restoring scroll position, and using the `OnScreenChangeListener`.

## Include to project
ModernAndroidPreferences is on [Bintray](https://bintray.com/maxr1998/maven/ModernAndroidPreferences), so you can get it like any other dependency via JCenter:
```gradle
dependencies {
    implementation 'de.Maxr1998.android:modernpreferences:1.1.0'
}
```

## License
Copyright © 2018-2020  Max Rumpf alias Maxr1998

This library is released under the Apache License version 2.0.
If you use this library (or code inspired by it) in your projects, crediting me is appreciated.

The example application however is licensed under the GNU General Public version 3, or any later version.