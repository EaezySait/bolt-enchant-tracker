# Bolt Enchant Tracker (BoltWise)

Current release: **v1.0.0**

![BoltWise session dashboard](docs/boltwise-panel.png)

A RuneLite Plugin Hub project that automatically detects bolt enchanting and shows live session speed, Magic XP, costs, estimated profit, and progress toward a target Magic level.

The plugin is observational only. It never clicks, casts, types, withdraws, equips, or otherwise performs game actions.

## Version 1 features

- Detects all ten gem bolt types in both standard and dragon-bolt variants
- Tracks bolts enchanted, spell casts, Magic XP, bolts/hour, and XP/hour
- Uses RuneLite guide prices for unfinished bolts, enchanted bolts, and required runes
- Supports actual buy and expected sell price overrides
- Includes optional Grand Exchange tax
- Accounts for free air, water, earth, or fire runes supplied by equipment
- Calculates session profit, profit/hour, and profit per 11,000 bolts
- Projects XP, bolts, time, and profit remaining to a selected Magic level
- Includes a statistics-only session reset in the sidebar
- Preserves the active session safely across world hops

## Important pricing note

Guide prices are estimates and may differ from actual Grand Exchange fills. Enter your actual buy price and intended sell price in the RuneLite plugin configuration for a more realistic projection. Reset the session when changing bolt types if price overrides are enabled.

If using a Tome of Fire or an appropriate elemental staff, enable the matching **Free runes** setting so those elemental runes are not counted as a cost.

## Development setup

1. Install IntelliJ IDEA Community Edition.
2. Open this folder as a Gradle project.
3. Set the project Gradle JVM to Eclipse Temurin JDK 11.
4. Allow the Gradle import to finish.
5. Run `BoltEnchantTrackerPluginTest` or the Gradle `run` task.
6. Log in through the RuneLite development-client flow for Jagex Accounts.
7. Enable **Bolt Enchant Tracker** in RuneLite.

## First in-game test

1. Open the BoltWise sidebar.
2. Set the target Magic level and any price overrides.
3. Enable **Free fire runes** if using a Tome of Fire.
4. Begin with a stack of unenchanted bolts already in your inventory.
5. Cast Enchant Crossbow Bolt once.
6. Confirm that the panel adds 10 bolts, one cast, and the correct spell XP.
7. Continue for several minutes and compare its XP/hour with RuneLite's XP Tracker.
8. Bank, withdraw, hop worlds, and reset the session to ensure those actions are not falsely counted.

## Planned follow-ups

- Side-by-side profitability comparison for every bolt type
- Low-volume and stale-price warnings
- Session history and personal-best rates
- Automatic detection of infinite elemental-rune equipment
- Notifications when another bolt becomes materially cheaper or more profitable

## License

BSD 2-Clause License. See `LICENSE`.
