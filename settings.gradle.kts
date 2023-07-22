dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "jamplate"

include("jamplate-jamtree")
include("jamplate-jamfn")
include("jamplate-jamcore")
