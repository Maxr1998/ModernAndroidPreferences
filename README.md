# ModernAndroidPreferences

[![GitHub release](https://img.shields.io/github/v/release/Maxr1998/ModernAndroidPreferences)](https://github.com/Maxr1998/ModernAndroidPreferences/releases)
[![Maven Central](https://img.shields.io/maven-central/v/de.maxr1998/modernandroidpreferences)](https://repo.maven.apache.org/maven2/de/maxr1998/modernandroidpreferences/)
[![Build status](https://img.shields.io/github/actions/workflow/status/Maxr1998/ModernAndroidPreferences/library-test.yaml?branch=master)](https://github.com/Maxr1998/ModernAndroidPreferences/actions/workflows/library-test.yaml)
[![Lint status](https://img.shields.io/github/actions/workflow/status/Maxr1998/ModernAndroidPreferences/library-lint.yaml?branch=master&label=detekt%20%26%20lint)](https://github.com/Maxr1998/ModernAndroidPreferences/actions/workflows/library-lint.yaml)
[![License](https://img.shields.io/github/license/Maxr1998/ModernAndroidPreferences)](https://github.com/Maxr1998/ModernAndroidPreferences/blob/master/LICENSE)

Android preferences in Kotlin DSL, displayed in a RecyclerView.

_No XML, no troubles with PreferenceManager, Fragments, or styling, no more ListView._ :tada:

### Code example
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

### Example app
Example Activities ([with](https://github.com/Maxr1998/ModernAndroidPreferences/tree/master/testapp/src/main/java/de/Maxr1998/modernpreferences/example/view_model) and [without](https://github.com/Maxr1998/ModernAndroidPreferences/blob/master/testapp/src/main/java/de/Maxr1998/modernpreferences/example/TestActivity.kt) using ViewModel)
show advanced info like back handling, saving/restoring scroll position, and using the `OnScreenChangeListener`.

### Screenshots
<details>
  <summary>Click to show</summary>

| ![](screenshots/screenshot_1.png) | ![](screenshots/screenshot_2.png) |
|:---------------------------------:|:---------------------------------:|

</details>

## Include to project
ModernAndroidPreferences is on [Maven Central](https://search.maven.org/artifact/de.maxr1998/modernandroidpreferences),
so you can get it like any other dependency:

```gradle
dependencies {
    implementation 'de.maxr1998:modernandroidpreferences:2.2.0'
}
```

---

**NOTE:** This library has previously been available as `de.Maxr1998.android:modernpreferences` on Bintray JCenter,
but was migrated to Sonatype Maven Central in light of the impending JCenter sunsetting.  
To get in line with Maven naming standards, it was renamed to `de.maxr1998:modernandroidpreferences`.

## License
Copyright © 2018-2021  Max Rumpf alias Maxr1998

This library is released under the Apache License version 2.0.
If you use this library (or code from it) in your projects, crediting me is appreciated.

The example application however is licensed under the GNU General Public version 3, or any later version.