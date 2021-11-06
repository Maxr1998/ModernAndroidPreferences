import java.util.*

enum class VersionType {
    STABLE, MILESTONE, UNSTABLE
}

fun classifyVersion(version: String): VersionType {
    val normalizedVersion = version.toLowerCase(Locale.ROOT)
    return when {
        normalizedVersion.containsAny(listOf("alpha", "beta", "dev")) -> VersionType.UNSTABLE
        normalizedVersion.containsAny(listOf("rc", "m")) -> VersionType.MILESTONE
        else -> VersionType.STABLE
    }
}

private fun String.containsAny(strings: Iterable<String>): Boolean = strings.any { contains(it) }