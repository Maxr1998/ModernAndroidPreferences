package de.Maxr1998.modernpreferences.testing

val uniqueKeySequence = iterator {
    var i = 0
    while (true) {
        yield("key_${i++}")
    }
}