package com.example.data

import com.example.model.CharacterArchetype
import com.example.model.ComicCharacter
import com.example.model.ComicGenre
import com.example.model.ComicSeries
import com.example.model.ComicStrip
import com.example.model.MoodTag
import com.example.model.StripPanel

object ComicCatalogPart2 {
    val series: List<ComicSeries> = listOf(
        ComicSeries(
            id = "owl_be_back",
            title = "Owl Be Back",
            creator = "Jonah Briar",
            syndicate = "Woodland Press",
            launchYear = 2008,
            runStatus = "Daily • Ongoing",
            schedule = "Daily",
            genre = ComicGenre.ANIMALS,
            rank = 7,
            subscribersLabel = "590K Readers",
            rating = 4.7,
            synopsis = "Professor Hoot runs the forest's only midnight library, dealing with insomniac squirrels, philosophical frogs, and morning birds who never whisper.",
            trivia = "Every background tree in the Sunday edition hides a tiny hidden acorn.",
            accentColorHex = 0xFF3949AB,
            mascotArchetype = CharacterArchetype.WISE_OWL,
            characters = listOf(
                ComicCharacter(
                    name = "Professor Hoot",
                    role = "Night Librarian",
                    bio = "Drinks chamomile at dawn and strongly dislikes chirpy 6 AM joggers.",
                    archetype = CharacterArchetype.WISE_OWL,
                    accentHex = 0xFF3949AB
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "obb_001",
                    seriesId = "owl_be_back",
                    stripNumber = 890,
                    title = "Early Bird Bias",
                    publishDate = "Nov 14, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.DEEP_THOUGHT, MoodTag.CUTE_PETS),
                    authorNote = "Written at 2:30 AM, naturally.",
                    panels = listOf(
                        StripPanel(
                            caption = "5:30 AM • Oak Branch",
                            speaker = "Professor Hoot",
                            dialogue = "They say the early bird gets the worm...",
                            archetype = CharacterArchetype.WISE_OWL,
                            pose = "thinking",
                            scene = "forest",
                            bgHex = 0xFFDFE6FF
                        ),
                        StripPanel(
                            speaker = "Professor Hoot",
                            dialogue = "Nobody ever mentions what happened to the early worm!",
                            sfx = "HOOT!",
                            archetype = CharacterArchetype.WISE_OWL,
                            pose = "shocked",
                            scene = "forest",
                            bgHex = 0xFFFFE7D1
                        ),
                        StripPanel(
                            speaker = "Professor Hoot",
                            thought = "Sleep in, woodland friends. It's self-preservation.",
                            archetype = CharacterArchetype.WISE_OWL,
                            pose = "sleepy",
                            scene = "forest",
                            bgHex = 0xFFE4F0EB
                        )
                    )
                ),
                ComicStrip(
                    id = "obb_002",
                    seriesId = "owl_be_back",
                    stripNumber = 891,
                    title = "Midnight Overdue Book",
                    publishDate = "Nov 19, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.CUTE_PETS, MoodTag.QUICK_LAUGH),
                    authorNote = "Library fines in acorns.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Professor Hoot",
                            dialogue = "This pinecone encyclopedia is three seasons overdue.",
                            archetype = CharacterArchetype.WISE_OWL,
                            pose = "smug",
                            scene = "forest",
                            bgHex = 0xFFE3ECFF
                        ),
                        StripPanel(
                            speaker = "Professor Hoot",
                            dialogue = "That will be two shiny pebbles and an apology.",
                            archetype = CharacterArchetype.WISE_OWL,
                            pose = "happy",
                            scene = "forest",
                            bgHex = 0xFFFFF4D2
                        ),
                        StripPanel(
                            speaker = "Professor Hoot",
                            thought = "Honestly, I just appreciate the pebbles.",
                            archetype = CharacterArchetype.WISE_OWL,
                            pose = "happy",
                            scene = "forest",
                            bgHex = 0xFFDFF7EE
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "minimalist_duck",
            title = "The Minimalist Duck",
            creator = "Sora Takahashi",
            syndicate = "Quiet Laugh Syndicate",
            launchYear = 2020,
            runStatus = "Daily • Ongoing",
            schedule = "Daily",
            genre = ComicGenre.HUMOR,
            rank = 8,
            subscribersLabel = "790K Readers",
            rating = 4.9,
            synopsis = "One deadpan duck floating on a calm pond, delivering razor-sharp observations about modern life in three crisp beats.",
            trivia = "Holds the record for fewest words per punchline in the syndicate.",
            accentColorHex = 0xFF00ACC1,
            mascotArchetype = CharacterArchetype.MINIMAL_DUCK,
            characters = listOf(
                ComicCharacter(
                    name = "Quack",
                    role = "Pond Philosopher",
                    bio = "Calm above the surface, paddling furiously beneath it—just like everyone else.",
                    archetype = CharacterArchetype.MINIMAL_DUCK,
                    accentHex = 0xFF00ACC1
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "md_001",
                    seriesId = "minimalist_duck",
                    stripNumber = 510,
                    title = "Productivity",
                    publishDate = "Nov 18, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.DEEP_THOUGHT, MoodTag.QUICK_LAUGH),
                    authorNote = "Keep paddling.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Quack",
                            dialogue = "Everyone says I look so peaceful floating here.",
                            archetype = CharacterArchetype.MINIMAL_DUCK,
                            pose = "happy",
                            scene = "park",
                            bgHex = 0xFFDFF6FA
                        ),
                        StripPanel(
                            speaker = "Quack",
                            dialogue = "Meanwhile, underwater my feet are doing 140 BPM cardio.",
                            sfx = "SPLASH!",
                            archetype = CharacterArchetype.MINIMAL_DUCK,
                            pose = "shocked",
                            scene = "park",
                            bgHex = 0xFFFFF3CC
                        ),
                        StripPanel(
                            speaker = "Quack",
                            dialogue = "Anyway, throw bread.",
                            archetype = CharacterArchetype.MINIMAL_DUCK,
                            pose = "smug",
                            scene = "park",
                            bgHex = 0xFFE2F5EE
                        )
                    )
                ),
                ComicStrip(
                    id = "md_002",
                    seriesId = "minimalist_duck",
                    stripNumber = 511,
                    title = "Five-Year Plan",
                    publishDate = "Nov 21, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH, MoodTag.DEEP_THOUGHT),
                    authorNote = "Valid goals.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Quack",
                            dialogue = "Someone asked me about my five-year career plan.",
                            archetype = CharacterArchetype.MINIMAL_DUCK,
                            pose = "thinking",
                            scene = "park",
                            bgHex = 0xFFE4F4FA
                        ),
                        StripPanel(
                            speaker = "Quack",
                            dialogue = "Year 1: Float. Year 2: Snack. Year 3: Float in new pond.",
                            archetype = CharacterArchetype.MINIMAL_DUCK,
                            pose = "happy",
                            scene = "park",
                            bgHex = 0xFFFFF2D6
                        ),
                        StripPanel(
                            speaker = "Quack",
                            thought = "Honestly, I'm ahead of schedule.",
                            archetype = CharacterArchetype.MINIMAL_DUCK,
                            pose = "smug",
                            scene = "park",
                            bgHex = 0xFFDFF7E6
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "captain_cardboard",
            title = "Captain Cardboard",
            creator = "Liam O'Connor",
            syndicate = "Sunday Ink Syndicate",
            launchYear = 1997,
            runStatus = "Sunday & Daily • Ongoing",
            schedule = "Daily & Sunday Color",
            genre = ComicGenre.ADVENTURE,
            rank = 9,
            subscribersLabel = "680K Readers",
            rating = 4.8,
            synopsis = "Armed with a towel cape and an invincible imagination, seven-year-old Pip turns ordinary grocery runs into dragon-slaying epics.",
            trivia = "Inspired by classic backyard adventure strips of the 1990s.",
            accentColorHex = 0xFFE53935,
            mascotArchetype = CharacterArchetype.KID_EXPLORER,
            characters = listOf(
                ComicCharacter(
                    name = "Pip",
                    role = "Defender of the Cul-de-sac",
                    bio = "Can defeat any broccoli monster with clever diplomacy.",
                    archetype = CharacterArchetype.KID_EXPLORER,
                    accentHex = 0xFFE53935
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "cap_001",
                    seriesId = "captain_cardboard",
                    stripNumber = 1420,
                    title = "The Lava Carpet",
                    publishDate = "Nov 16, 2026",
                    isSundayColor = true,
                    moodTags = listOf(MoodTag.SUNDAY_COLOR, MoodTag.NOSTALGIA),
                    authorNote = "Standard living room survival rules.",
                    panels = listOf(
                        StripPanel(
                            caption = "Living Room Crossing",
                            speaker = "Pip",
                            dialogue = "Red alert! The rug has turned into molten magma!",
                            sfx = "WHOOSH!",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "heroic",
                            scene = "room",
                            bgHex = 0xFFFFDFD6
                        ),
                        StripPanel(
                            speaker = "Pip",
                            dialogue = "Couch cushion bridge deployed! Steady... steady!",
                            sfx = "BOING!",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "shocked",
                            scene = "room",
                            bgHex = 0xFFFFF0C7
                        ),
                        StripPanel(
                            speaker = "Pip",
                            dialogue = "Mission complete! Now to raid the cookie vault.",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "happy",
                            scene = "kitchen",
                            bgHex = 0xFFDFF7EC
                        )
                    )
                ),
                ComicStrip(
                    id = "cap_002",
                    seriesId = "captain_cardboard",
                    stripNumber = 1421,
                    title = "Snow Day",
                    publishDate = "Nov 22, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.NOSTALGIA, MoodTag.QUICK_LAUGH),
                    authorNote = "Best two words in the English language.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Pip",
                            dialogue = "Radio says school is canceled! Sound the victory horn!",
                            sfx = "TOOT!",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "heroic",
                            scene = "park",
                            bgHex = 0xFFDFEEFF
                        ),
                        StripPanel(
                            speaker = "Pip",
                            dialogue = "Wait... why is there only half an inch of frost?",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "thinking",
                            scene = "park",
                            bgHex = 0xFFE8EEF7
                        ),
                        StripPanel(
                            speaker = "Pip",
                            thought = "Never question a gift from the Weather Council.",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "smug",
                            scene = "park",
                            bgHex = 0xFFFFF3CE
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "suburban_ufo",
            title = "Suburban UFO",
            creator = "Tanya Patel",
            syndicate = "Orbit Comics",
            launchYear = 2022,
            runStatus = "Tue / Thu / Sun",
            schedule = "3x Weekly",
            genre = ComicGenre.HUMOR,
            rank = 10,
            subscribersLabel = "510K Readers",
            rating = 4.6,
            synopsis = "Zorp crash-landed in a quiet neighborhood and decided to stay after discovering garlic naan and garage sales.",
            trivia = "Zorp's flying saucer is currently registered as a 2004 hatchback.",
            accentColorHex = 0xFF43A047,
            mascotArchetype = CharacterArchetype.ALIEN_GUEST,
            characters = listOf(
                ComicCharacter(
                    name = "Zorp",
                    role = "Neighborhood Alien",
                    bio = "Trying desperately to blend in by wearing oversized sunglasses and mowing his lawn with a laser.",
                    archetype = CharacterArchetype.ALIEN_GUEST,
                    accentHex = 0xFF43A047
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "su_001",
                    seriesId = "suburban_ufo",
                    stripNumber = 144,
                    title = "Garage Sale Artifacts",
                    publishDate = "Nov 19, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH, MoodTag.TECH_GEEK),
                    authorNote = "One human's junk is an alien's warp core.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Zorp",
                            dialogue = "Earthling! What is this dazzling spinning disc for $1?",
                            archetype = CharacterArchetype.ALIEN_GUEST,
                            pose = "shocked",
                            scene = "park",
                            bgHex = 0xFFE4F8E8
                        ),
                        StripPanel(
                            speaker = "Zorp",
                            thought = "Clearly an ancient data crystal storing planetary secrets!",
                            sfx = "SCAN!",
                            archetype = CharacterArchetype.ALIEN_GUEST,
                            pose = "thinking",
                            scene = "park",
                            bgHex = 0xFFFFF4CE
                        ),
                        StripPanel(
                            caption = "Back in the saucer",
                            speaker = "Zorp",
                            dialogue = "Wait... it's a 1999 AOL free trial CD. Still shiny though.",
                            archetype = CharacterArchetype.ALIEN_GUEST,
                            pose = "happy",
                            scene = "space",
                            bgHex = 0xFFDFEBFF
                        )
                    )
                ),
                ComicStrip(
                    id = "su_002",
                    seriesId = "suburban_ufo",
                    stripNumber = 145,
                    title = "Small Talk Protocol",
                    publishDate = "Nov 22, 2026",
                    isSundayColor = true,
                    moodTags = listOf(MoodTag.SUNDAY_COLOR, MoodTag.QUICK_LAUGH),
                    authorNote = "Weather talk is universal.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Zorp",
                            dialogue = "Greetings, neighbor! Quite a barometric pressure fluctuation today!",
                            archetype = CharacterArchetype.ALIEN_GUEST,
                            pose = "happy",
                            scene = "park",
                            bgHex = 0xFFE3F7EA
                        ),
                        StripPanel(
                            speaker = "Zorp",
                            thought = "Did I nail human small talk? They said 'Yeah, crazy weather!'",
                            archetype = CharacterArchetype.ALIEN_GUEST,
                            pose = "smug",
                            scene = "park",
                            bgHex = 0xFFFFF5D4
                        ),
                        StripPanel(
                            speaker = "Zorp",
                            dialogue = "Earth camouflage: 100% undetected.",
                            sfx = "HIGH-5!",
                            archetype = CharacterArchetype.ALIEN_GUEST,
                            pose = "heroic",
                            scene = "park",
                            bgHex = 0xFFDCEBFF
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "retro_diner",
            title = "Retro Diner '79",
            creator = "Hank Miller",
            syndicate = "Classic Funnies Guild",
            launchYear = 1979,
            runStatus = "Classic Archive • 1979–2004",
            schedule = "Archival Daily",
            genre = ComicGenre.RETRO,
            rank = 11,
            subscribersLabel = "920K Readers",
            rating = 4.8,
            synopsis = "Warm coffee refills, jukebox debates, and neighborhood banter inside a neon-lit highway diner where time moves slower.",
            trivia = "Ran continuously for 25 years across 380 newspapers.",
            accentColorHex = 0xFFFB8C00,
            mascotArchetype = CharacterArchetype.TABBY_CAT,
            characters = listOf(
                ComicCharacter(
                    name = "Biscuit",
                    role = "Diner Alley Cat",
                    bio = "Officially the health inspector's nemesis, unofficially the diner mascot.",
                    archetype = CharacterArchetype.TABBY_CAT,
                    accentHex = 0xFFFB8C00
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "rd_001",
                    seriesId = "retro_diner",
                    stripNumber = 4012,
                    title = "Jukebox Economics",
                    publishDate = "Jun 12, 1984",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.NOSTALGIA, MoodTag.QUICK_LAUGH),
                    authorNote = "From the 1984 Summer Archive.",
                    panels = listOf(
                        StripPanel(
                            caption = "Diner Counter • 1984",
                            speaker = "Biscuit",
                            dialogue = "A quarter for three songs, and you played the same track thrice?",
                            archetype = CharacterArchetype.TABBY_CAT,
                            pose = "smug",
                            scene = "kitchen",
                            bgHex = 0xFFFFEBD2
                        ),
                        StripPanel(
                            speaker = "Biscuit",
                            dialogue = "Respectable. Carry on with the pancakes.",
                            sfx = "SIZZLE",
                            archetype = CharacterArchetype.TABBY_CAT,
                            pose = "happy",
                            scene = "kitchen",
                            bgHex = 0xFFFFF4CF
                        ),
                        StripPanel(
                            speaker = "Biscuit",
                            thought = "Just drop one hashbrown near the stool. Nobody saw nothing.",
                            archetype = CharacterArchetype.TABBY_CAT,
                            pose = "sleepy",
                            scene = "kitchen",
                            bgHex = 0xFFEAE5DC
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "sunday_porch",
            title = "Sunday Porch",
            creator = "Ananya Sen",
            syndicate = "Heartland Features",
            launchYear = 2005,
            runStatus = "Daily • Ongoing",
            schedule = "Daily & Sunday",
            genre = ComicGenre.FAMILY,
            rank = 12,
            subscribersLabel = "540K Readers",
            rating = 4.8,
            synopsis = "Gentle, relatable stories about multigenerational family chaos, monsoon tea, lost TV remotes, and neighborhood cricket.",
            trivia = "Readers frequently frame the annual Diwali and Thanksgiving Sunday strips.",
            accentColorHex = 0xFFF4511E,
            mascotArchetype = CharacterArchetype.GRANNY_PUNK,
            characters = listOf(
                ComicCharacter(
                    name = "Dadi",
                    role = "Porch Philosopher",
                    bio = "Knows where every lost key in the house is without standing up.",
                    archetype = CharacterArchetype.GRANNY_PUNK,
                    accentHex = 0xFFF4511E
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "sp_001",
                    seriesId = "sunday_porch",
                    stripNumber = 940,
                    title = "The Remote Conspiracy",
                    publishDate = "Nov 18, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH, MoodTag.NOSTALGIA),
                    authorNote = "Happens in every living room on Earth.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Dadi",
                            dialogue = "Have you looked behind the blue cushion?",
                            archetype = CharacterArchetype.GRANNY_PUNK,
                            pose = "thinking",
                            scene = "room",
                            bgHex = 0xFFFFEFE0
                        ),
                        StripPanel(
                            speaker = "Dadi",
                            dialogue = "No, not that cushion. The one you're sitting on.",
                            archetype = CharacterArchetype.GRANNY_PUNK,
                            pose = "smug",
                            scene = "room",
                            bgHex = 0xFFFFF4D1
                        ),
                        StripPanel(
                            speaker = "Dadi",
                            dialogue = "That will be one cup of ginger chai as a finder's fee.",
                            sfx = "TADA!",
                            archetype = CharacterArchetype.GRANNY_PUNK,
                            pose = "happy",
                            scene = "kitchen",
                            bgHex = 0xFFDFF7EE
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "star_pilot_zero",
            title = "Star Pilot Zero",
            creator = "Kai Mercer",
            syndicate = "Orbit Comics",
            launchYear = 2015,
            runStatus = "Weekly • Ongoing",
            schedule = "Fridays & Sundays",
            genre = ComicGenre.ADVENTURE,
            rank = 13,
            subscribersLabel = "470K Readers",
            rating = 4.7,
            synopsis = "Ace pilot Nova delivers interstellar parcels across weird galaxies, mostly dodging space traffic and rogue vending machines.",
            trivia = "Every ship name in the series is named after a pastry.",
            accentColorHex = 0xFF1565C0,
            mascotArchetype = CharacterArchetype.SPACE_PILOT,
            characters = listOf(
                ComicCharacter(
                    name = "Nova",
                    role = "Galactic Courier",
                    bio = "Flies the SS Croissant at warp 4 with zero sense of direction.",
                    archetype = CharacterArchetype.SPACE_PILOT,
                    accentHex = 0xFF1565C0
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "spz_001",
                    seriesId = "star_pilot_zero",
                    stripNumber = 288,
                    title = "Same-Day Galactic Delivery",
                    publishDate = "Nov 20, 2026",
                    isSundayColor = true,
                    moodTags = listOf(MoodTag.SUNDAY_COLOR, MoodTag.TECH_GEEK),
                    authorNote = "Nebula traffic is always worst at 5 PM.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Nova",
                            dialogue = "Only 2 parsecs left and 4 minutes on the delivery clock!",
                            sfx = "ZOOM!",
                            archetype = CharacterArchetype.SPACE_PILOT,
                            pose = "heroic",
                            scene = "space",
                            bgHex = 0xFFDCE8FF
                        ),
                        StripPanel(
                            caption = "Space Toll Booth",
                            speaker = "Nova",
                            dialogue = "Exact change in antimatter quarters?! Who carries those?!",
                            archetype = CharacterArchetype.SPACE_PILOT,
                            pose = "shocked",
                            scene = "space",
                            bgHex = 0xFFFFE4E1
                        ),
                        StripPanel(
                            speaker = "Nova",
                            dialogue = "Left the parcel behind a moon rock. Snapped delivery photo. Boom.",
                            archetype = CharacterArchetype.SPACE_PILOT,
                            pose = "smug",
                            scene = "space",
                            bgHex = 0xFFDDF6EE
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "deadline_espresso",
            title = "Deadline Espresso",
            creator = "Priya Nair",
            syndicate = "Metro Tribune Comics",
            launchYear = 2018,
            runStatus = "Weekdays • Ongoing",
            schedule = "Mon–Fri",
            genre = ComicGenre.OFFICE,
            rank = 14,
            subscribersLabel = "430K Readers",
            rating = 4.6,
            synopsis = "Inside an indie design studio where clients ask to 'make the logo pop more' and fonts are argued over like high art.",
            trivia = "Comic Sans appears only once in strip #100 as a Halloween villain.",
            accentColorHex = 0xFF6D4C41,
            mascotArchetype = CharacterArchetype.TIRED_DEV,
            characters = listOf(
                ComicCharacter(
                    name = "Rhea",
                    role = "Lead Designer",
                    bio = "Can spot a 1-pixel misalignment from across the street.",
                    archetype = CharacterArchetype.TIRED_DEV,
                    accentHex = 0xFF6D4C41
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "de_001",
                    seriesId = "deadline_espresso",
                    stripNumber = 402,
                    title = "Final_v2_REAL_FINAL",
                    publishDate = "Nov 19, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH, MoodTag.TECH_GEEK),
                    authorNote = "Never name a file 'final'.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Rhea",
                            dialogue = "Exported! 'Campaign_Poster_FINAL.pdf'. Time to celebrate.",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "happy",
                            scene = "office",
                            bgHex = 0xFFFFF4D6
                        ),
                        StripPanel(
                            caption = "5 minutes later",
                            speaker = "Rhea",
                            dialogue = "Client email: 'Love it! Just one tiny tweak to the entire layout...'",
                            sfx = "PING!",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "shocked",
                            scene = "office",
                            bgHex = 0xFFFFE1DF
                        ),
                        StripPanel(
                            speaker = "Rhea",
                            thought = "Saving as Campaign_Poster_FINAL_v7_ACTUALLY_DONE.pdf.",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "sleepy",
                            scene = "office",
                            bgHex = 0xFFEAE7F5
                        )
                    )
                )
            )
        )
    )
}
