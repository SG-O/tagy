# Structure Definition

## Basic Format
The structure definition is a JSON array of tag definition objects. 
These are the name/value pairs applicable to all Types:

"key":          REQUIRED - This is the key for the tag. 
Must use only alphanumeric characters (A-Z, a-z, 0-9), underscore (_) and minus (-).

"type":         REQUIRED - This defines the type the value of the tag will use. 
Must be one of "LONG", "DOUBLE", "ENUM", "STRING", "DATE" or "LIST".

"name":         OPTIONAL - The title/name describing the value. If not set, the key will be used.

"description":  OPTIONAL - Describes the tag.

"required":     OPTIONAL - If true the value must be set. Defaults to false.

"tagEnabler":   OPTIONAL - Allows the input to only be shown when a specific Enum entry is selected. See TagEnabler for details.

## Types

### Long

    {
        "key": "someKey",
        "type": "LONG",
        "name": "Tage Name",
        "min": 0,
        "max": 100,
        "description": "Tag Description",
        "required": true,
        "parameter": "IN",
        "tagEnabler":
            {
                "selectorKey": "enabler",
                "enumEntry": 0
            }
    }

"min":          OPTIONAL - The minimum allowed value (inclusive).

"max":          OPTIONAL - The maximum allowed value (inclusive).

"parameter":    OPTIONAL - See parameter for details.

### Double

    {
        "key": "someKey",
        "type": "DOUBLE",
        "name": "Tage Name",
        "min": 0.0,
        "max": 100.0,
        "description": "Tag Description",
        "required": true,
        "parameter": "OUT",
        "tagEnabler":
            {
                "selectorKey": "enabler",
                "enumEntry": 0
            }
    }

"min":          OPTIONAL - The minimum allowed value (inclusive).

"max":          OPTIONAL - The maximum allowed value (inclusive).

"parameter":    OPTIONAL - See Parameter for details.

### Boolean

    {
        "key": "someKey",
        "type": "BOOLEAN",
        "name": "Tage Name",
        "description": "Tag Description",
        "required": true,
        "tagEnabler":
            {
                "selectorKey": "enabler",
                "enumEntry": 0
            }
    }

### Enum

    {
        "key": "someKey",
        "type": "ENUM",
        "name": "Tage Name",
        "description": "Tag Description",
        "required": true,
        "enumerators": 
            [
                "Option 1",
                "Option 2",
                "Option 3"
            ],
        "tagEnabler":
            {
                "selectorKey": "enabler",
                "enumEntry": 0
            }
    }

"enumerators":  OPTIONAL - A JSON array of strings with the enumerators.

### String

    {
        "key": "someKey",
        "type": "STRING",
        "name": "Tage Name",
        "description": "Tag Description",
        "required": true,
        "parameter": "LENGTH",
        "tagEnabler":
            {
                "selectorKey": "enabler",
                "enumEntry": 0
            }
    }

"parameter":    OPTIONAL - See parameter for details.

### Date

    {
        "key": "someKey",
        "type": "DATE",
        "name": "Tage Name",
        "description": "Tag Description",
        "required": true,
        "tagEnabler":
            {
                "selectorKey": "enabler",
                "enumEntry": 0
            }
    }

### List

    {
        "key": "someKey",
        "type": "LIST",
        "name": "Tage Name",
        "description": "Tag Description",
        "required": true,
        "internal":
            {
                "key": "someKey",
                "type": "STRING",
                "name": "Tage Name",
                "description": "Tag Description",
                "required": true
            },
        "tagEnabler":
            {
                "selectorKey": "enabler",
                "enumEntry": 0
            }
    }

"internal":     REQUIRED - The tag definition of the values contained in the list.

## Parameter

The parameter can give values a special meaning with special features:

"IN":           The start point of an area of interest. In ms for audio/video.

"OUT":          The start point of an area of interest. In ms for audio/video.

"LENGTH":       The total length of the data. In ms for audio/video.

## TagEnabler

Allows the input to only be shown when a specific Enum entry is selected.

"selectorKey":  The key of the enum to act as the selector. Must reference an Enum or a List of Enums.
"enumEntry":    The Enum entry that should make the input visible.

## Example

Here is an example with the annotation UI it generates.

    [
        {
            "key": "cat",
            "type": "LIST",
            "name": "Category",
            "description": "Chose what describes the video best.",
            "required": false,
            "enumerators": [],
            "internal": 
            {
                "key": "",
                "type": "ENUM",
                "required": true,
                "enumerators": 
                    [
                        "Outdoors",
                        "Landscape",
                        "Technology"
                    ]
            }
        },
        {
            "key": "score",
            "type": "DOUBLE",
            "name": "Score",
            "description": "0 = Disliked, 100 = Liked",
            "min": 0.0,
            "max": 100.0,
            "required": true,
            "enumerators": []
        },
        {
            "key": "in",
            "type": "LONG",
            "name": "Start",
            "required": true,
            "enumerators": [],
            "parameter": "IN"
        },
        {
            "key": "out",
            "type": "LONG",
            "name": "End",
            "required": false,
            "enumerators": [],
            "parameter": "OUT"
        },
        {
            "key": "length",
            "type": "LONG",
            "name": "Length",
            "required": true,
            "enumerators": [],
            "parameter": "LENGTH"
        },
        {
            "key": "thoughts",
            "type": "STRING",
            "name": "Your Thoughts",
            "required": false,
            "enumerators": []
        },
        {
            "key": "created",
            "type": "DATE",
            "name": "Creation",
            "required": true,
            "enumerators": []
        }
    ]

![Output](https://raw.githubusercontent.com/SG-O/tagy/master/doc/example01.png "Output")