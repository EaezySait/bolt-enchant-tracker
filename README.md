# Bolt Enchant Tracker (BoltWise)

![BoltWise session dashboard](docs/boltwise-panel.png)

BoltWise automatically detects bolt enchanting and displays live training speed, Magic XP, costs, profit or loss, and progress toward your target Magic level.

## Features

- Tracks enchanted bolts, spell casts, Magic XP, bolts per hour, and XP per hour
- Supports all ten gem bolt types in both standard and dragon-bolt variants
- Uses RuneLite guide prices for bolts and required runes
- Supports custom buy and sell prices for more accurate calculations
- Includes optional Grand Exchange tax
- Accounts for free elemental runes supplied by equipment
- Calculates session profit, profit per hour, and profit per 11,000 bolts
- Projects the XP, bolts, time, and profit remaining to your selected Magic level
- Preserves your active session across world hops
- Includes a manual session reset

## Getting started

1. Install **Bolt Enchant Tracker** from RuneLite's Plugin Hub.
2. Enable the plugin and open the **BoltWise** sidebar panel.
3. Choose your target Magic level in the plugin settings.
4. Configure the Grand Exchange tax, price overrides, and any free elemental runes.
5. Enchant bolts normally—BoltWise will detect them automatically.

## Pricing accuracy

RuneLite guide prices are estimates and may differ from your actual Grand Exchange trades. For the most accurate profit projection, enter the price you paid for the unenchanted bolts and the price you expect to receive for the enchanted bolts.

If a tome or elemental staff supplies runes for free, enable the matching **Free runes** setting so BoltWise does not count those runes as a cost.

When switching bolt types while using custom price overrides, reset the session and update the prices for the new bolt type.

## Support and feedback

Found a bug or have a suggestion? [Open an issue on GitHub](https://github.com/EaezySait/bolt-enchant-tracker/issues).

BoltWise observes game state and calculates statistics only. It does not click, type, cast, bank, equip items, or perform any other game action.

## License

Licensed under the BSD 2-Clause License. See [LICENSE](LICENSE).
