package com.example.data

import com.example.model.CharacterArchetype
import com.example.model.ComicCharacter
import com.example.model.ComicGenre
import com.example.model.ComicSeries
import com.example.model.ComicStrip
import com.example.model.MoodTag
import com.example.model.StripPanel

object ComicCatalogPart1 {
    val series: List<ComicSeries> = listOf(
        ComicSeries(
            id = "marmalade_toast",
            title = "Marmalade & Toast",
            creator = "Rohan Kulkarni",
            syndicate = "Sunday Ink Syndicate",
            launchYear = 1994,
            runStatus = "Daily • Ongoing",
            schedule = "Daily & Sunday Color",
            genre = ComicGenre.ANIMALS,
            rank = 1,
            subscribersLabel = "1.4M Readers",
            rating = 4.9,
            synopsis = "Marmalade is a philosophically lazy ginger tabby who believes every Monday is an unprovoked personal attack. Toast is his over-caffeinated human roommate.",
            trivia = "Originally sketched on coffee napkins; Marmalade has slept through 3,200 alarm clocks in canon.",
            accentColorHex = 0xFFFF8F00,
            coverDrawableName = "img_cover_cats",
            mascotArchetype = CharacterArchetype.ORANGE_CAT,
            characters = listOf(
                ComicCharacter(
                    name = "Marmalade",
                    role = "Chief Nap Officer",
                    bio = "Values buttered toast, sunbeams, and ignoring his name unless a can opener clicks.",
                    archetype = CharacterArchetype.ORANGE_CAT,
                    accentHex = 0xFFFF8F00
                ),
                ComicCharacter(
                    name = "Toast",
                    role = "Sleep-Deprived Human",
                    bio = "Spends 40% of his paycheck on artisanal coffee and cat toys Marmalade refuses to touch.",
                    archetype = CharacterArchetype.TIRED_DEV,
                    accentHex = 0xFF1F6AF7
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "mt_001",
                    seriesId = "marmalade_toast",
                    stripNumber = 1042,
                    title = "Gravity Audit",
                    publishDate = "Nov 18, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH, MoodTag.CUTE_PETS),
                    authorNote = "Based on an actual mug incident at 7:02 AM.",
                    panels = listOf(
                        StripPanel(
                            caption = "7:00 AM • Kitchen Counter",
                            speaker = "Toast",
                            dialogue = "Please don't touch my mug, buddy.",
                            archetype = CharacterArchetype.TIRED_DEV,
                            secondaryArchetype = CharacterArchetype.ORANGE_CAT,
                            pose = "thinking",
                            scene = "kitchen",
                            bgHex = 0xFFFFF3DC
                        ),
                        StripPanel(
                            speaker = "Marmalade",
                            thought = "Hmm. Is gravity still subscribed today?",
                            sfx = "TAP...",
                            archetype = CharacterArchetype.ORANGE_CAT,
                            pose = "smug",
                            scene = "kitchen",
                            bgHex = 0xFFFFEAC4
                        ),
                        StripPanel(
                            speaker = "Marmalade",
                            dialogue = "Good news: physics still works. Bad news: your shoes are caffeinated.",
                            sfx = "CRASH!",
                            archetype = CharacterArchetype.ORANGE_CAT,
                            secondaryArchetype = CharacterArchetype.TIRED_DEV,
                            pose = "happy",
                            scene = "kitchen",
                            bgHex = 0xFFFFDFB5
                        )
                    )
                ),
                ComicStrip(
                    id = "mt_002",
                    seriesId = "marmalade_toast",
                    stripNumber = 1043,
                    title = "The Luxury Cardboard",
                    publishDate = "Nov 19, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.CUTE_PETS, MoodTag.QUICK_LAUGH),
                    authorNote = "Every pet owner knows the $60 bed rule.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Toast",
                            dialogue = "I bought you a velvet orthopedic cat throne!",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "happy",
                            scene = "room",
                            bgHex = 0xFFEAF3FF
                        ),
                        StripPanel(
                            speaker = "Marmalade",
                            thought = "Velvet? Amateur. Look at that glorious Amazon shipping box.",
                            archetype = CharacterArchetype.ORANGE_CAT,
                            pose = "smug",
                            scene = "room",
                            bgHex = 0xFFFFF5D9
                        ),
                        StripPanel(
                            caption = "Five seconds later",
                            speaker = "Marmalade",
                            dialogue = "Tell the bank I am unreachable in my fortress.",
                            sfx = "RUSTLE!",
                            archetype = CharacterArchetype.ORANGE_CAT,
                            pose = "sleepy",
                            scene = "room",
                            bgHex = 0xFFFFE5B8
                        )
                    )
                ),
                ComicStrip(
                    id = "mt_003",
                    seriesId = "marmalade_toast",
                    stripNumber = 1044,
                    title = "Sunday Morning Negotiations",
                    publishDate = "Nov 22, 2026",
                    isSundayColor = true,
                    moodTags = listOf(MoodTag.SUNDAY_COLOR, MoodTag.CUTE_PETS),
                    authorNote = "Sunday special double-tier edition!",
                    panels = listOf(
                        StripPanel(
                            caption = "Sunday 5:45 AM",
                            speaker = "Marmalade",
                            dialogue = "Rise and shine! The bottom of my bowl is 4% visible.",
                            sfx = "MEOW!!",
                            archetype = CharacterArchetype.ORANGE_CAT,
                            pose = "heroic",
                            scene = "room",
                            bgHex = 0xFFFFE4C7
                        ),
                        StripPanel(
                            speaker = "Toast",
                            dialogue = "Marmalade, the sun hasn't even clocked in yet...",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "sleepy",
                            scene = "room",
                            bgHex = 0xFFE2E8FF
                        ),
                        StripPanel(
                            speaker = "Marmalade",
                            dialogue = "Shake the bowl. I don't need new kibble, I need the illusion of abundance.",
                            archetype = CharacterArchetype.ORANGE_CAT,
                            pose = "smug",
                            scene = "kitchen",
                            bgHex = 0xFFFFF1C9
                        ),
                        StripPanel(
                            caption = "5:47 AM",
                            speaker = "Marmalade",
                            thought = "Ah, freshly shaken kibble. A delicacy.",
                            sfx = "CRUNCH",
                            archetype = CharacterArchetype.ORANGE_CAT,
                            pose = "happy",
                            scene = "kitchen",
                            bgHex = 0xFFDFF7EE
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "cosmos_bolt",
            title = "Cosmos & Bolt",
            creator = "Elena Vance",
            syndicate = "Starlight Press",
            launchYear = 2012,
            runStatus = "Mon–Sat • Ongoing",
            schedule = "6 Days a Week",
            genre = ComicGenre.SCI_FI,
            rank = 2,
            subscribersLabel = "980K Readers",
            rating = 4.8,
            synopsis = "Ten-year-old stargazer Leo builds a cardboard spaceship that somehow actually reaches Jupiter—accompanied by Bolt, a toaster-brained robot with anxiety.",
            trivia = "Voted Best Sci-Fi Strip three years running; Bolt runs on 2 AA batteries and optimism.",
            accentColorHex = 0xFF1F6AF7,
            coverDrawableName = "img_cover_space",
            mascotArchetype = CharacterArchetype.BOXY_ROBOT,
            characters = listOf(
                ComicCharacter(
                    name = "Leo",
                    role = "Backyard Captain",
                    bio = "Armed with a red cape, flashlight, and total disregard for orbital mechanics.",
                    archetype = CharacterArchetype.KID_EXPLORER,
                    accentHex = 0xFFE53935
                ),
                ComicCharacter(
                    name = "Bolt",
                    role = "Co-Pilot / Toaster",
                    bio = "Calculates a 99.4% chance of getting grounded before dinner.",
                    archetype = CharacterArchetype.BOXY_ROBOT,
                    accentHex = 0xFF009688
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "cb_001",
                    seriesId = "cosmos_bolt",
                    stripNumber = 612,
                    title = "Hyperdrive Homework",
                    publishDate = "Nov 17, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.TECH_GEEK, MoodTag.QUICK_LAUGH),
                    authorNote = "Math homework always bends space-time.",
                    panels = listOf(
                        StripPanel(
                            caption = "Orbit of Saturn",
                            speaker = "Bolt",
                            dialogue = "Captain! Asteroid storm ahead! Also your math sheet is still blank!",
                            sfx = "BEEP!",
                            archetype = CharacterArchetype.BOXY_ROBOT,
                            pose = "shocked",
                            scene = "space",
                            bgHex = 0xFFDFEAFF
                        ),
                        StripPanel(
                            speaker = "Leo",
                            dialogue = "No worries! Near a black hole, time slows down. That gives me infinite minutes to finish!",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "heroic",
                            scene = "space",
                            bgHex = 0xFFD3E4FF
                        ),
                        StripPanel(
                            speaker = "Bolt",
                            dialogue = "Counterpoint: your mom operates on Earth Standard Time.",
                            sfx = "BZZT!",
                            archetype = CharacterArchetype.BOXY_ROBOT,
                            secondaryArchetype = CharacterArchetype.KID_EXPLORER,
                            pose = "thinking",
                            scene = "space",
                            bgHex = 0xFFE6E0FF
                        )
                    )
                ),
                ComicStrip(
                    id = "cb_002",
                    seriesId = "cosmos_bolt",
                    stripNumber = 613,
                    title = "Alien Parking Ticket",
                    publishDate = "Nov 20, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.TECH_GEEK, MoodTag.QUICK_LAUGH),
                    authorNote = "No planet escapes municipal zoning.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Leo",
                            dialogue = "First human on Moon-9! Look at this pristine crater!",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "happy",
                            scene = "space",
                            bgHex = 0xFFE2F5F2
                        ),
                        StripPanel(
                            speaker = "Zorp",
                            dialogue = "Excuse me, Earthling. This is a 15-minute comet loading zone.",
                            sfx = "ZAP!",
                            archetype = CharacterArchetype.ALIEN_GUEST,
                            pose = "smug",
                            scene = "space",
                            bgHex = 0xFFE5FAEE
                        ),
                        StripPanel(
                            speaker = "Bolt",
                            dialogue = "Do you accept cosmic lint or half a peanut butter sandwich?",
                            archetype = CharacterArchetype.BOXY_ROBOT,
                            pose = "shocked",
                            scene = "space",
                            bgHex = 0xFFFFF2D6
                        )
                    )
                ),
                ComicStrip(
                    id = "cb_003",
                    seriesId = "cosmos_bolt",
                    stripNumber = 614,
                    title = "Firmware Update",
                    publishDate = "Nov 22, 2026",
                    isSundayColor = true,
                    moodTags = listOf(MoodTag.SUNDAY_COLOR, MoodTag.TECH_GEEK),
                    authorNote = "Never update your co-pilot mid-flight.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Leo",
                            dialogue = "Bolt, engage thrusters! We're racing the Comet Express!",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "heroic",
                            scene = "space",
                            bgHex = 0xFFDCE7FF
                        ),
                        StripPanel(
                            speaker = "Bolt",
                            dialogue = "Unable to comply. Installing Update 14 of 89. Do not turn off your robot.",
                            sfx = "WHIRR...",
                            archetype = CharacterArchetype.BOXY_ROBOT,
                            pose = "sleepy",
                            scene = "space",
                            bgHex = 0xFFE9E7FF
                        ),
                        StripPanel(
                            caption = "Two hours later",
                            speaker = "Bolt",
                            dialogue = "Update complete! New feature: I can now generate pastel pie charts of our doom.",
                            archetype = CharacterArchetype.BOXY_ROBOT,
                            secondaryArchetype = CharacterArchetype.KID_EXPLORER,
                            pose = "happy",
                            scene = "space",
                            bgHex = 0xFFDFF7EE
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "cubicle_cactus",
            title = "Cubicle Cactus",
            creator = "Meera Deshmukh",
            syndicate = "Metro Tribune Comics",
            launchYear = 2016,
            runStatus = "Weekdays • Ongoing",
            schedule = "Mon–Fri",
            genre = ComicGenre.OFFICE,
            rank = 3,
            subscribersLabel = "860K Readers",
            rating = 4.8,
            synopsis = "A silent potted cactus named Spike watches office workers survive endless video calls, broken printers, and suspiciously optimistic Monday emails.",
            trivia = "Spike has received three employee-of-the-month mugs purely by never replying-all.",
            accentColorHex = 0xFF009688,
            coverDrawableName = "img_cover_office",
            mascotArchetype = CharacterArchetype.POTTED_CACTUS,
            characters = listOf(
                ComicCharacter(
                    name = "Spike",
                    role = "Senior Desk Plant",
                    bio = "Needs water once a month. Listens to everyone's vent sessions with prickly grace.",
                    archetype = CharacterArchetype.POTTED_CACTUS,
                    accentHex = 0xFF009688
                ),
                ComicCharacter(
                    name = "Dev",
                    role = "Product Analyst",
                    bio = "Communicates fluently in sighing, sticky notes, and lukewarm espresso.",
                    archetype = CharacterArchetype.TIRED_DEV,
                    accentHex = 0xFFE53935
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "cc_001",
                    seriesId = "cubicle_cactus",
                    stripNumber = 408,
                    title = "Could Have Been an Email",
                    publishDate = "Nov 16, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH, MoodTag.DEEP_THOUGHT),
                    authorNote = "Every calendar invite has a hidden boss battle.",
                    panels = listOf(
                        StripPanel(
                            caption = "9:00 AM • Desk 4B",
                            speaker = "Dev",
                            dialogue = "A 60-minute 'Quick Sync' just landed on my calendar, Spike.",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "shocked",
                            scene = "office",
                            bgHex = 0xFFFFF4DC
                        ),
                        StripPanel(
                            speaker = "Spike",
                            thought = "Watch him nod on camera while secretly reading comic strips.",
                            archetype = CharacterArchetype.POTTED_CACTUS,
                            pose = "smug",
                            scene = "office",
                            bgHex = 0xFFE4F6F1
                        ),
                        StripPanel(
                            caption = "10:02 AM",
                            speaker = "Dev",
                            dialogue = "The entire meeting concluded with: 'Let's take this offline via email.'",
                            sfx = "SIGH...",
                            archetype = CharacterArchetype.TIRED_DEV,
                            secondaryArchetype = CharacterArchetype.POTTED_CACTUS,
                            pose = "sleepy",
                            scene = "office",
                            bgHex = 0xFFFFE5DF
                        )
                    )
                ),
                ComicStrip(
                    id = "cc_002",
                    seriesId = "cubicle_cactus",
                    stripNumber = 409,
                    title = "Printer Whisperer",
                    publishDate = "Nov 19, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH),
                    authorNote = "Printers sense fear.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Dev",
                            dialogue = "I just need ONE boarding pass printed. Be cool, machine.",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "thinking",
                            scene = "office",
                            bgHex = 0xFFEAF1FF
                        ),
                        StripPanel(
                            caption = "Display Screen: 'LOW MAGENTA INK'",
                            speaker = "Dev",
                            dialogue = "IT'S A BLACK AND WHITE BARCODE!",
                            sfx = "ERROR!",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "shocked",
                            scene = "office",
                            bgHex = 0xFFFFDCDC
                        ),
                        StripPanel(
                            speaker = "Spike",
                            thought = "That's why I prefer photosynthesis. Zero toner cartridges.",
                            archetype = CharacterArchetype.POTTED_CACTUS,
                            pose = "happy",
                            scene = "office",
                            bgHex = 0xFFDFF7EE
                        )
                    )
                ),
                ComicStrip(
                    id = "cc_003",
                    seriesId = "cubicle_cactus",
                    stripNumber = 410,
                    title = "Friday Energy",
                    publishDate = "Nov 21, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH),
                    authorNote = "4:55 PM on Friday hits different.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Dev",
                            dialogue = "4:58 PM on Friday. Clean inbox. Weekend mode activated.",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "happy",
                            scene = "office",
                            bgHex = 0xFFFFF6D6
                        ),
                        StripPanel(
                            caption = "Ping! 'Got a quick sec?'",
                            speaker = "Dev",
                            dialogue = "...",
                            sfx = "PING!",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "shocked",
                            scene = "office",
                            bgHex = 0xFFFFDFDF
                        ),
                        StripPanel(
                            speaker = "Spike",
                            thought = "And just like that, he became a houseplant too.",
                            archetype = CharacterArchetype.POTTED_CACTUS,
                            pose = "smug",
                            scene = "office",
                            bgHex = 0xFFE2F5F1
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "paws_clues",
            title = "Paws & Clues",
            creator = "Arthur Pendelton",
            syndicate = "Classic Funnies Guild",
            launchYear = 1988,
            runStatus = "Classic Archive • Complete",
            schedule = "Daily & Sunday",
            genre = ComicGenre.RETRO,
            rank = 4,
            subscribersLabel = "1.1M Readers",
            rating = 4.9,
            synopsis = "Detective Barnaby is a trench-coat beagle who solves suburban mysteries—almost all of which end in him eating the evidence.",
            trivia = "One of the beloved Sunday paper classics from the golden late-80s era.",
            accentColorHex = 0xFF795548,
            mascotArchetype = CharacterArchetype.DOG_DETECTIVE,
            characters = listOf(
                ComicCharacter(
                    name = "Barnaby",
                    role = "Private Beagle",
                    bio = "Never takes off his magnifying glass, even when chasing the mail truck.",
                    archetype = CharacterArchetype.DOG_DETECTIVE,
                    accentHex = 0xFF8D6E63
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "pc_001",
                    seriesId = "paws_clues",
                    stripNumber = 2210,
                    title = "Case of the Missing Bacon",
                    publishDate = "Oct 14, 1991",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.NOSTALGIA, MoodTag.CUTE_PETS),
                    authorNote = "From the 1991 Fall Sunday Vault.",
                    panels = listOf(
                        StripPanel(
                            caption = "Detective Log #2210",
                            speaker = "Barnaby",
                            dialogue = "The plate is clean. Not a crumb left. Truly the work of a mastermind.",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "thinking",
                            scene = "kitchen",
                            bgHex = 0xFFF5EBDC
                        ),
                        StripPanel(
                            speaker = "Barnaby",
                            thought = "Wait... why are my paws smelling suspiciously smoky?",
                            sfx = "SNIFF!",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "shocked",
                            scene = "kitchen",
                            bgHex = 0xFFFFE9CC
                        ),
                        StripPanel(
                            speaker = "Barnaby",
                            dialogue = "Case closed. The culprit was clearly framed by deliciousness.",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "smug",
                            scene = "kitchen",
                            bgHex = 0xFFE9F3DF
                        )
                    )
                ),
                ComicStrip(
                    id = "pc_002",
                    seriesId = "paws_clues",
                    stripNumber = 2211,
                    title = "The Squirrel Alibi",
                    publishDate = "Oct 15, 1991",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.NOSTALGIA, MoodTag.QUICK_LAUGH),
                    authorNote = "Archival ink print.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Barnaby",
                            dialogue = "I had the backyard perimeter locked down tight at 2:00 PM.",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "heroic",
                            scene = "park",
                            bgHex = 0xFFE4F4DC
                        ),
                        StripPanel(
                            speaker = "Barnaby",
                            dialogue = "Then a squirrel waved one acorn, and I forgot my own name.",
                            sfx = "WOOF!",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "happy",
                            scene = "park",
                            bgHex = 0xFFFFF2CE
                        ),
                        StripPanel(
                            caption = "Report Filed",
                            speaker = "Barnaby",
                            thought = "Next time I bring backup treats.",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "sleepy",
                            scene = "park",
                            bgHex = 0xFFEDE8DF
                        )
                    )
                ),
                ComicStrip(
                    id = "pc_003",
                    seriesId = "paws_clues",
                    stripNumber = 2212,
                    title = "Sunday Trench Coat",
                    publishDate = "Oct 20, 1991",
                    isSundayColor = true,
                    moodTags = listOf(MoodTag.NOSTALGIA, MoodTag.SUNDAY_COLOR),
                    authorNote = "Restored 1991 Sunday Color print.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Barnaby",
                            dialogue = "Rainy Sunday. Perfect weather for brooding on the porch.",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "thinking",
                            scene = "room",
                            bgHex = 0xFFDAE4F5
                        ),
                        StripPanel(
                            speaker = "Barnaby",
                            dialogue = "Unless someone says the T-R-E-A-T word.",
                            sfx = "EARS UP!",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "shocked",
                            scene = "room",
                            bgHex = 0xFFFFEAB8
                        ),
                        StripPanel(
                            speaker = "Barnaby",
                            dialogue = "Brooding postponed indefinitely!",
                            archetype = CharacterArchetype.DOG_DETECTIVE,
                            pose = "happy",
                            scene = "room",
                            bgHex = 0xFFDFF7E8
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "ctrl_alt_sigh",
            title = "Ctrl+Alt+Sigh",
            creator = "Nikhil & Sam",
            syndicate = "PixelStrip Network",
            launchYear = 2019,
            runStatus = "3x Weekly • Ongoing",
            schedule = "Mon / Wed / Fri",
            genre = ComicGenre.SCI_FI,
            rank = 5,
            subscribersLabel = "720K Readers",
            rating = 4.7,
            synopsis = "Two programmers debug legacy code written by themselves six months ago—a terrifying mystery novel with no comments.",
            trivia = "Famous for the 'Works on My Machine' commemorative trophy strip.",
            accentColorHex = 0xFF5E35B1,
            mascotArchetype = CharacterArchetype.TIRED_DEV,
            characters = listOf(
                ComicCharacter(
                    name = "Sam",
                    role = "Backend Sleeper",
                    bio = "Writes regexes that even compilers apologize to.",
                    archetype = CharacterArchetype.TIRED_DEV,
                    accentHex = 0xFF5E35B1
                ),
                ComicCharacter(
                    name = "Stick-Bot",
                    role = "Rubber Duck",
                    bio = "Answers every question with pure silence, somehow fixing 80% of bugs.",
                    archetype = CharacterArchetype.STICK_GENIUS,
                    accentHex = 0xFF1F6AF7
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "cas_001",
                    seriesId = "ctrl_alt_sigh",
                    stripNumber = 315,
                    title = "Time Traveler's Comment",
                    publishDate = "Nov 15, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.TECH_GEEK, MoodTag.QUICK_LAUGH),
                    authorNote = "Always write comments for future you.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Sam",
                            dialogue = "Who wrote this terrifying function with zero documentation?!",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "shocked",
                            scene = "office",
                            bgHex = 0xFFEDE7FF
                        ),
                        StripPanel(
                            caption = "$ git blame main.kt",
                            speaker = "Stick-Bot",
                            dialogue = "Author: Sam. Six months ago. Commit message: 'quick fix lol'.",
                            sfx = "BLOOP",
                            archetype = CharacterArchetype.STICK_GENIUS,
                            pose = "smug",
                            scene = "office",
                            bgHex = 0xFFE3ECFF
                        ),
                        StripPanel(
                            speaker = "Sam",
                            dialogue = "Past Sam was a villain. Future Sam can handle this.",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "sleepy",
                            scene = "office",
                            bgHex = 0xFFFFE8E8
                        )
                    )
                ),
                ComicStrip(
                    id = "cas_002",
                    seriesId = "ctrl_alt_sigh",
                    stripNumber = 316,
                    title = "Dark Mode Supremacy",
                    publishDate = "Nov 18, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.TECH_GEEK, MoodTag.DEEP_THOUGHT),
                    authorNote = "Protect your retinas at all costs.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Stick-Bot",
                            dialogue = "Why do programmers love dark mode?",
                            archetype = CharacterArchetype.STICK_GENIUS,
                            pose = "thinking",
                            scene = "office",
                            bgHex = 0xFFE2E6F2
                        ),
                        StripPanel(
                            speaker = "Sam",
                            dialogue = "Because light attracts bugs!",
                            sfx = "BA-DUM!",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "happy",
                            scene = "office",
                            bgHex = 0xFFFFF4CC
                        ),
                        StripPanel(
                            speaker = "Stick-Bot",
                            thought = "And yet the compiler warnings remain neon yellow.",
                            archetype = CharacterArchetype.STICK_GENIUS,
                            pose = "smug",
                            scene = "office",
                            bgHex = 0xFFE6F4F1
                        )
                    )
                ),
                ComicStrip(
                    id = "cas_003",
                    seriesId = "ctrl_alt_sigh",
                    stripNumber = 317,
                    title = "The Semicolon Hunt",
                    publishDate = "Nov 20, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.TECH_GEEK, MoodTag.QUICK_LAUGH),
                    authorNote = "Four hours for one character.",
                    panels = listOf(
                        StripPanel(
                            caption = "2:00 AM",
                            speaker = "Sam",
                            dialogue = "400 lines checked. Still failing. Maybe computers were a mistake.",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "sleepy",
                            scene = "office",
                            bgHex = 0xFFEAE4F7
                        ),
                        StripPanel(
                            speaker = "Stick-Bot",
                            dialogue = "Check line 12. You typed a Greek question mark instead of a semicolon.",
                            sfx = "PING!",
                            archetype = CharacterArchetype.STICK_GENIUS,
                            pose = "happy",
                            scene = "office",
                            bgHex = 0xFFDDF5EE
                        ),
                        StripPanel(
                            speaker = "Sam",
                            dialogue = "BUILD SUCCEEDED. I am a golden god of silicon!",
                            sfx = "WOOT!",
                            archetype = CharacterArchetype.TIRED_DEV,
                            pose = "heroic",
                            scene = "office",
                            bgHex = 0xFFFFF1C7
                        )
                    )
                )
            )
        ),
        ComicSeries(
            id = "granny_glitch",
            title = "Granny Glitch",
            creator = "Clara Chen",
            syndicate = "Arcade Funnies Co.",
            launchYear = 2021,
            runStatus = "Daily • Ongoing",
            schedule = "Daily",
            genre = ComicGenre.FAMILY,
            rank = 6,
            subscribersLabel = "640K Readers",
            rating = 4.9,
            synopsis = "Seventy-four-year-old Grandma Bea knits wool sweaters by morning and speedruns retro RPGs by afternoon while giving life advice to her grandkids.",
            trivia = "Bea holds the neighborhood high score in Tetris and Lemon Loaf baking.",
            accentColorHex = 0xFFD81B60,
            mascotArchetype = CharacterArchetype.GRANNY_PUNK,
            characters = listOf(
                ComicCharacter(
                    name = "Grandma Bea",
                    role = "Speedrun Matriarch",
                    bio = "Can parry a boss attack without dropping a knitting stitch.",
                    archetype = CharacterArchetype.GRANNY_PUNK,
                    accentHex = 0xFFD81B60
                ),
                ComicCharacter(
                    name = "Milo",
                    role = "Player Two",
                    bio = "Still trying to beat Bea's high score from 1998.",
                    archetype = CharacterArchetype.KID_EXPLORER,
                    accentHex = 0xFF1F6AF7
                )
            ),
            strips = listOf(
                ComicStrip(
                    id = "gg_001",
                    seriesId = "granny_glitch",
                    stripNumber = 198,
                    title = "Boss Fight & Biscuits",
                    publishDate = "Nov 17, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH, MoodTag.TECH_GEEK),
                    authorNote = "Never underestimate Granny on the D-pad.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Milo",
                            dialogue = "Gran, this dragon boss is impossible! I've tried ten times!",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "shocked",
                            scene = "room",
                            bgHex = 0xFFFFE5EE
                        ),
                        StripPanel(
                            speaker = "Grandma Bea",
                            dialogue = "Hand me the controller, sweetie. Also the oven timer just dinged.",
                            sfx = "CLICK-CLACK",
                            archetype = CharacterArchetype.GRANNY_PUNK,
                            pose = "smug",
                            scene = "room",
                            bgHex = 0xFFFFF2D6
                        ),
                        StripPanel(
                            caption = "42 seconds later",
                            speaker = "Grandma Bea",
                            dialogue = "Dragon defeated. No-damage run. Now eat a warm cinnamon scone.",
                            sfx = "VICTORY!",
                            archetype = CharacterArchetype.GRANNY_PUNK,
                            secondaryArchetype = CharacterArchetype.KID_EXPLORER,
                            pose = "heroic",
                            scene = "kitchen",
                            bgHex = 0xFFDFF7EE
                        )
                    )
                ),
                ComicStrip(
                    id = "gg_002",
                    seriesId = "granny_glitch",
                    stripNumber = 199,
                    title = "Cloud Storage",
                    publishDate = "Nov 21, 2026",
                    isSundayColor = false,
                    moodTags = listOf(MoodTag.QUICK_LAUGH, MoodTag.DEEP_THOUGHT),
                    authorNote = "Cookie tins are eternal.",
                    panels = listOf(
                        StripPanel(
                            speaker = "Milo",
                            dialogue = "Where do you back up your photos, Gran? The Cloud?",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "thinking",
                            scene = "room",
                            bgHex = 0xFFE3EEFF
                        ),
                        StripPanel(
                            speaker = "Grandma Bea",
                            dialogue = "Better. The blue Danish butter cookie tin.",
                            archetype = CharacterArchetype.GRANNY_PUNK,
                            pose = "happy",
                            scene = "room",
                            bgHex = 0xFFFFF5CC
                        ),
                        StripPanel(
                            speaker = "Milo",
                            thought = "Right next to the sewing needles. Maximum encryption.",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "smug",
                            scene = "room",
                            bgHex = 0xFFFCE4EC
                        )
                    )
                ),
                ComicStrip(
                    id = "gg_003",
                    seriesId = "granny_glitch",
                    stripNumber = 200,
                    title = "Milestone #200",
                    publishDate = "Nov 22, 2026",
                    isSundayColor = true,
                    moodTags = listOf(MoodTag.SUNDAY_COLOR, MoodTag.QUICK_LAUGH),
                    authorNote = "Celebrating 200 strips of Granny Glitch!",
                    panels = listOf(
                        StripPanel(
                            speaker = "Grandma Bea",
                            dialogue = "People ask my secret to staying young at 74.",
                            archetype = CharacterArchetype.GRANNY_PUNK,
                            pose = "happy",
                            scene = "park",
                            bgHex = 0xFFFFE9F0
                        ),
                        StripPanel(
                            speaker = "Grandma Bea",
                            dialogue = "Daily walks, green tea, and trash-talking polite robots online.",
                            sfx = "GG EZ!",
                            archetype = CharacterArchetype.GRANNY_PUNK,
                            pose = "heroic",
                            scene = "park",
                            bgHex = 0xFFFFF3CC
                        ),
                        StripPanel(
                            speaker = "Milo",
                            dialogue = "Honestly? Iconic.",
                            archetype = CharacterArchetype.KID_EXPLORER,
                            pose = "happy",
                            scene = "park",
                            bgHex = 0xFFDFF7EE
                        )
                    )
                )
            )
        )
    )
}
