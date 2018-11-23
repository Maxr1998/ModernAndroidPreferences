# ModernAndroidPreferences
Android Preferences defined through Kotlin DSL, shown in a RecyclerView.  
_No XML, no awful PreferenceManager, Fragments or styling problems, no more ListView._ :tada:

#### Syntax example
```Kotlin
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
}
```

#### Create preference adapter
```Kotlin
val preferencesAdapter = PreferencesAdapter(screen)
```

#### View the example app
Example Activities ([with](https://github.com/Maxr1998/ModernAndroidPreferences/tree/master/testapp/src/main/java/de/Maxr1998/modernpreferences/example/view_model) and [without](https://github.com/Maxr1998/ModernAndroidPreferences/blob/master/testapp/src/main/java/de/Maxr1998/modernpreferences/example/TestActivity.kt) using ViewModel)
show advanced info like back handling, saving/restoring scroll position, and using the `OnScreenChangeListener`.

## Include to project
You can get ModernAndroidPreferences through [JitPack](https://jitpack.io/#Maxr1998/ModernAndroidPreferences/-SNAPSHOT):
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

…

dependencies {
    implementation 'com.github.Maxr1998:ModernAndroidPreferences:-SNAPSHOT'
}
```

## License
Copyright © 2018  Max Rumpf alias Maxr1998

This library is released under the Apache License version 2.0.
If you use this library (or code inspired by it) in your projects, crediting me is appreciated.

The example application however is licensed under the GNU General Public version 3, or any later version.