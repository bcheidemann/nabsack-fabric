# Nabsack Fabric Mod

This README is for developers. For usage, see [USAGE.md](USAGE.md).

## Setup (VS Code)

1. Install the recommended plugins for the repo
2. Import the gradle project. If no prompt appears open `build.gradle`
3. Generate the `launch.json` file using
   ```sh
   ./gradlew vscode
   ```

You can now launch Minecraft with the mod loaded by selecting one of the options from the "Run and Debug" menu.

Optionally, if you want to browse the Minecraft source, follow these steps:

1. Generate the Minecraft source
   ```sh
   ./gradlew genSources
   ```
2. Refresh your Java project by opening `build.gradle` and pressing `Shift + Alt + U`

You can now search for Minecraft classes by pressing `Ctrl + P` and typing `#` followed by your search term.

## Migrating Mappings

Mappings can be automatically updated by running:

```sh
gradlew migrateMappings --mappings "<version>"
```

To find the appropriate value of `<version>`, go to [https://fabricmc.net/develop/](https://fabricmc.net/develop/).

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
