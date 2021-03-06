import uk.co.cacoethes.util.NameType

String projectId = ask("What is the ID of the project such as 'micronaut-aws-sdk' [$projectDir.name]:", projectDir.name, "id")

String githubOrg = ask("What is your GitHub organization or username [$projectDir.parentFile.name]:", projectDir.parentFile.name, "org")

String slug = "$githubOrg/$projectId"

String groupSuggestion = 'com.' + githubOrg.replaceAll('[-_]', '.')
String group = ask("What is the Maven coordinates group of the new project [$groupSuggestion]:", groupSuggestion, "group")

String pgkSuggestion = (group + '.' + projectId).replaceAll('[-_]', '.')
String pkg = ask("What is the base package of the new project [$pgkSuggestion]:", pgkSuggestion, "pkg")

String nameSuggestion = transformText(projectId, from: NameType.HYPHENATED, to: NameType.NATURAL)
String name = ask("What is the human readable name of the new project [$nameSuggestion]:", nameSuggestion, "name")

String descriptionSuggestion = "$nameSuggestion Library"
String description = ask("What is the description of the project [$descriptionSuggestion]:", descriptionSuggestion, "desc")

String vendorSuggestion = transformText(githubOrg, from: NameType.HYPHENATED, to: NameType.NATURAL)
String vendor = ask("What is the human readable vendor name of your organization [$vendorSuggestion]:", vendorSuggestion, "vendor")

String devName = ''

try {
    devName = 'git config --get user.name'.execute().text.trim()
} catch (Exception e) {
    // no git present
}

if (devName) {
    devName = ask("Who are you? [$devName]:", devName, "dev.name")
}

while (!devName) {
    devName = ask("Who are you?", '', "dev.name")
}

String devIdSuggestion = System.getenv('LOGNAME') ?:  System.getenv('USER')
String devId = ask("What is your preferred user id (nickname) [$devIdSuggestion]:", devIdSuggestion, "dev.id")
Map attrs = [
    org: githubOrg,
    projectId: projectId,
    name: name,
    vendor: vendor,
    desc: description,
    devId: devId,
    devName: devName,
    slug: slug,
    group: group
]

processTemplates "settings.gradle", attrs
processTemplates "build.gradle", attrs
processTemplates "gradle.properties", attrs
processTemplates "README.md", attrs

File firstSubproject = new File(projectDir, "libs/$projectId")
firstSubproject.mkdirs()

File subprojectGradleFile = new File(firstSubproject, "${projectId}.gradle")
subprojectGradleFile.text = """
dependencies {
    // add project's dependencies
}
"""

new File(firstSubproject, "src/main/groovy/${pkg.replace('.', '/')}").mkdirs()
new File(firstSubproject, "src/main/resources/").mkdirs()
new File(firstSubproject, "src/test/groovy/${pkg.replace('.', '/')}").mkdirs()
new File(firstSubproject, "src/test/resources/").mkdirs()

File firstExampleSubproject = new File(projectDir, "examples/$projectId-example")
firstExampleSubproject.mkdirs()

File exampleSubprojectGradleFile = new File(firstExampleSubproject, "${projectId}-example.gradle")
exampleSubprojectGradleFile.text = """
dependencies {
    implementation project(":$projectId")

    // add example project's dependencies
}
"""

new File(firstExampleSubproject, "src/main/groovy/${pkg.replace('.', '/')}/example").mkdirs()
new File(firstExampleSubproject, "src/main/resources/").mkdirs()
new File(firstExampleSubproject, "src/test/groovy/${pkg.replace('.', '/')}/example").mkdirs()
new File(firstExampleSubproject, "src/test/resources/").mkdirs()


File gitignore = new File(projectDir, '.gitignore')
gitignore.text = """
# Gradle
build/
.gradle/

# IDEs

*.c9
*.iml
*.ipr
*.iws
*.vscode
.idea/
.asscache
MANIFEST.MF
out

# PAW
*.paw

# Redis
*.rdb

"""


