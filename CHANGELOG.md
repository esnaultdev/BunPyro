# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

## [0.5.0] - 2021-08-24
### Added
- Notifications for new reviews
- Audio can be auto played during reviews
- Bunny mode is available for reviews

### Updated
- Cleaned up the settings screen to fit the new settings
- Better color contrast in dark mode

### Fixed
- Fixed a rare crash during reviews
- Playing the audio of an example no longer resets its expanded state
- For multiple answer fields in reviews, all fields are colored
- The precision displayed in the review screen did not match the one of the summary

## [0.4.1] - 2021-08-03
### Fixed
- Fixed a crash when searching for grammar with filters

## [0.4.0] - 2021-08-01
### Added
- Review actions for grammar points (add, remove, reset)
- In app grammar reviews

### Updated
- Better splash screen
- Better display for SRS progress in lists

### Fixed
- Wrong kana conversion for romaji input in search
- Failing to parse the API data when `order` fields are missing
- Fix the furigana animations getting stuck on some devices

## [0.3.7] - 2020-04-15
### Fixed
- Grammar filters issues
- New audio not playing

## [0.3.6] - 2020-04-14
### Added
- SRS details in lists (available behind a setting)
- Golden seal for burned items
- SRS and JLPT info in the grammar point details

### Fixed
- Fixed some text contrast in dark mode
- JLPT progress sometimes disappearing
- Color issues on older devices

## [0.3.5] - 2020-04-10
### Added
- Audio player for examples

## [0.3.4] - 2020-04-08
### Fixed
- Some examples were not highlighted properly

## [0.3.3] - 2020-04-07
### Added
- Studied and not studied filters for all grammar
- Setting to control the default visibility of example details
- Review button with review count (that redirects to Bunpro for now)
- Better UX for the manual sync
- Version in settings

### Fixed
- Prevent navigation to an incomplete grammar point

## [0.3.2] - 2020-04-01
### Added
- Selectable text for a grammar point's name, details (structure, caution, ...) and examples

## [0.3.1] - 2020-03-31
### Added
- Highlight query in search results using color

### Fixed
- Fixed the sync not working for some users
- Fixed the filter dialog auto closing after navigating back

## [0.3.0] - 2020-03-29
### Added
- Manual sync option
- Search headers to separate romaji search from the base search
- JLPT tags in the search results
- JLPT filters for all grammar
- Highlight query in search results
- Navigation icons on most screens

### Fixed
- Fixed more color issues on SDK 21
- Fixed the lessons progress being cropped
- Fixed a crash when searching for punctuation
- Fixed a crash when opening the app after a long time
- Fixed the display of grammar with html content in lists

## [0.2.1] - 2020-03-27
### Fixed
- Fixed some search issues with romaji

## [0.2.0] - 2020-03-23
### Added
- Ability to copy the example text
- Setting for the default furigana display

### Fixed
- Fix some color issues on SDK 21
- Hide text scrollbars when toggling off furigana in example sentences

## [0.1.3] - 2020-03-22
### Added
- Non fatal exceptions logging using crashlytics

### Fixed
- Fix crashes in release builds due to proguard

## [0.1.2] - 2020-03-21
### Fixed
- Fix a crash on lower SDK devices due to a regex not compiling
- Fix a crash for SDK 21 due to a drawable/color mismatch

## [0.1.1] - 2020-03-20
### Added
- This CHANGELOG file
- Crashlytics to diagnose some crashes reported by an alpha user

## [0.1.0] - 2020-03-19
### Added
- Basic app with one time sync, search, lessons and grammar points
