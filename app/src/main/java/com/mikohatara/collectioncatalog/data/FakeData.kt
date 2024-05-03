package com.mikohatara.collectioncatalog.data

val samplePlates = listOf(
    Plate(
        CommonDetails(
            "Finland",
            null,
            null,
            "Sample",
            "1996 - 2001",
            null
        ),
        UniqueDetails(
            "ABC-012",
            'a',
            null,
            null,
            "2023-04-21",
            5.0,
            35.0,
            null
        ),
        Availability(
            true,
            false
        ),
        Source(
            null,
            null,
            null,
            "Online",
            "Finland"
        )
    ),
    Plate(
        CommonDetails(
            "Finland",
            null,
            null,
            "Sample",
            "1996 - 2001",
            null
        ),
        UniqueDetails(
            "ABC-012",
            'b',
            null,
            null,
            "2023-04-21",
            5.0,
            35.0,
            null
        ),
        Availability(
            false,
            true
        ),
        Source(
            null,
            null,
            null,
            "Online",
            "Finland"
        )
    ),
    Plate(
        CommonDetails(
            "Japan",
            "Sample",
            null,
            "Sample",
            "1998 - ",
            null
        ),
        UniqueDetails(
            "見本480 あ12-34",
            'a',
            null,
            null,
            null,
            null,
            null,
            null
        ),
        Availability(
            true,
            false
        ),
        Source(
            null,
            null,
            null,
            null,
            null
        )
    ),
    Plate(
        CommonDetails(
            "Japan",
            "Sample",
            null,
            "Sample",
            "1998 - ",
            null
        ),
        UniqueDetails(
            "見本50A さ01-23",
            'a',
            null,
            null,
            null,
            null,
            null,
            null
        ),
        Availability(
            true,
            false
        ),
        Source(
            null,
            null,
            null,
            null,
            null
        )
    ),
    Plate(
        CommonDetails(
            "Soviet Union",
            "Sample",
            null,
            "Sample",
            "1998 - ",
            null
        ),
        UniqueDetails(
            "1234 АБВ",
            'a',
            null,
            null,
            null,
            null,
            null,
            null
        ),
        Availability(
            true,
            false
        ),
        Source(
            null,
            null,
            null,
            null,
            null
        )
    ),
    Plate(
        CommonDetails(
            "Soviet Union",
            "Sample",
            null,
            "Sample",
            "1998 - ",
            null
        ),
        UniqueDetails(
            "А 1234 БВ",
            'a',
            null,
            null,
            null,
            null,
            null,
            null
        ),
        Availability(
            true,
            false
        ),
        Source(
            null,
            null,
            null,
            null,
            null
        )
    )
)