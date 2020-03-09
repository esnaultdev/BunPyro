This is an unofficial documentation of the BunPro API.

Note that an official documentation is available on the [BunPro website](https://bunpro.jp/api).

GET api/user/{USER_API_KEY}/
----------------------------

Information about the user (no request).

Example response

```
{
    "user_information": {
        "username":"matthieuesnault",
        "grammar_point_count":191,
        "ghost_review_count":24,
        "creation_date":1528066276
    },
    "requested_information":null
}
```

GET api/user/{USER_API_KEY}/study_queue
---------------------------------------

Information about when the next reviews are.

Example response

```
{
    "user_information": {
        "username": "matthieuesnault",
        "grammar_point_count": 191,
        "ghost_review_count": 24,
        "creation_date": 1528066276

    },
    "requested_information": {
        "reviews_available": 21,
        "next_review_date": 1582432200,
        "reviews_available_next_hour": 21,
        "reviews_available_next_day": 30
    }
}
```

GET api/user/{USER_API_KEY}/recent_items/{LIMIT}
------------------------------------------------

Information about recently added grammar.

Limit: number of items to return (between 0 and 50, defaults to 10).

Example response

```
{
    "user_information": {
        "username": "matthieuesnault",
        "grammar_point_count": 191,
        "ghost_review_count": 24,
        "creation_date": 1528066276

    },
    "requested_information": [
        {
            "grammar_point": "ている③",
            "created_at_date": 1.582.351.671,
            "updated_at_date": 1.582.429.278

        },
            "grammar_point": "ている②",
            "created_at_date": 1.582.351.655,
            "updated_at_date": 1.582.429.159

        },
            "grammar_point": "はずがない",
            "created_at_date": 1.582.279.274,
            "updated_at_date": 1.582.429.298
        }
    ]
}
```

GET api/v4/user
---------------
*Also available in v3*

Information about the user.

Example query
```
curl -H "Authorization: Bearer {USER_API_KEY}" "https://bunpro.jp/api/v3/user"
```

Example response
```
{
  "id": 1,
  "hide_english": "No",
  "furigana": "Off",
  "username": "matthieuesnault",
  "light_mode": "Modern Dark",
  "bunny_mode": "Off",
  "new_reviews": [],
  "review_english": "Show",
  "subscriber": true
}
```

Possible values
```
"hide_english": "Yes", "No"
"furigana": "On", "Off", "Wanikani"
"light_mode": "Modern", "Modern Dark", "Classic"
"bunny_mode": "On", "Off"
"review_english": "Hide", "Hint", "Show", "More", "Always Show Nuance"
```

GET api/v3/user/progress
------------------------

Information about the progress of JLPT levels.

Example query
```
curl -H "Authorization: Bearer {USER_API_KEY}" "https://bunpro.jp/api/v3/user/progress"
```

Example response
```
{
    "N5": [
        108,
        108
    ],
    "N4": [
        82,
        171
    ],
    "N3": [
        1,
        219
    ],
    "N2": [
        0,
        108
    ]
}
```

GET api/v4/reviews/all_reviews_total
------------------------------------

Information about all the reviews.

Example query
```
curl -H "Authorization: Bearer {USER_API_KEY}" "https://bunpro.jp/api/v4/reviews/all_reviews_total"
```

Example response
```
{
    "reviews": [
        {
            "id": 1,
            "user_id": 1,
            "study_question_id": 3178,
            "grammar_point_id": 161,
            "times_correct": 2,
            "times_incorrect": 2,
            "streak": 0,
            "next_review": "2020-02-23T04:30:00.000Z",
            "created_at": "2020-02-21T10:01:14.727Z",
            "updated_at": "2020-02-23T03:41:38.964Z",
            "readings": [],
            "complete": true,
            "last_studied_at": "2020-02-23T03:41:38.958Z",
            "was_correct": false,
            "self_study": false,
            "review_misses": 0,
            "history": [
                {
                    "id": 310,
                    "time": "2020-02-21 10:00:00 +0000",
                    "status": true,
                    "attempts": 1,
                    "streak": 1
                },
                {
                    "id": 311,
                    "time": "2020-02-21 16:00:00 +0000",
                    "status": false,
                    "attempts": 2,
                    "streak": 0
                },
                {
                    "id": 312,
                    "time": "2020-02-22 02:00:00 +0000",
                    "status": true,
                    "attempts": 1,
                    "streak": 1
                },
                {
                    "id": 3177,
                    "time": "2020-02-23 03:00:00 +0000",
                    "status": false,
                    "attempts": 2,
                    "streak": 0
                }
            ],
            "missed_question_ids": [
                311,
                3177
            ],
            "studied_question_ids": [
                310,
                311,
                312,
                3177
            ],
            "review_type": "standard",
            "max_streak": 1
        },
        [...]
    ],
    "ghost_reviews": [
        {
            "id": 2,
            "user_id": 1,
            "study_question_id": 628,
            "grammar_point_id": 11,
            "history": [
                {
                    "id": 628,
                    "time": "2019-03-31 21:00:00 +0000",
                    "status": true,
                    "attempts": 1,
                    "streak": 1
                },
                {
                    "id": 628,
                    "time": "2019-04-01 10:00:00 +0000",
                    "status": true,
                    "attempts": 1,
                    "streak": 2
                },
                {
                    "id": 628,
                    "time": "2019-04-02 11:00:00 +0000",
                    "status": true,
                    "attempts": 1,
                    "streak": 3
                },
                {
                    "id": 628,
                    "time": "2019-04-04 22:00:00 +0000",
                    "status": true,
                    "attempts": 1,
                    "streak": 4
                }
            ],
            "times_correct": 4,
            "times_incorrect": 0,
            "streak": 4,
            "next_review": "2039-04-04T22:00:00.000Z",
            "last_studied_at": "2019-04-04T22:23:43.854Z",
            "review_misses": 0,
            "was_correct": true,
            "self_study": false,
            "created_at": "2019-03-31T16:54:39.563Z",
            "updated_at": "2019-04-04T22:23:43.856Z",
            "review_type": "ghost"
        },
        [...]
    ],
    "self_study_reviews": []
}
```

GET api/v4/lessons
------------------

Information about the lessons, which the grammar points use for organisation.

Example query
```
curl -H "Authorization: Bearer {USER_API_KEY}" "https://bunpro.jp/api/v4/lessons/"
```

Example response
```
{
    "data": [
        {
            "id": "1",
            "type": "lessons",
            "attributes": {
                "jlpt-level": 5
            }
        },
        [...]
        {
            "id": "50",
            "type": "lessons",
            "attributes": {
                "jlpt-level": 1
            }
        }
    ]
}
```

GET api/v4/grammar_points
-------------------------

Information about the grammar points.

```
curl -H "Authorization: Bearer {USER_API_TOKEN}" "https://bunpro.jp/api/v4/grammar_points"
```

Example response
```
{
    "data": [
        {
            "id": "5",
            "type": "grammar-points",
            "attributes": {
                "title": "これ",
                "yomikata": "これ",
                "meaning": "this",
                "caution": "",
                "structure": "Demonstrative",
                "level": "JLPT5",
                "lesson-id": 1,
                "nuance": "[object near the speaker]",
                "incomplete": false,
                "grammar-order": 5
            }
        },
        [...]
        {
            "id": "416",
            "type": "grammar-points",
            "attributes": {
                "title": "Verb[て]",
                "yomikata": "て-form",
                "meaning": "Verb [て-form]",
                "caution": "For future reference: You may encounter Verb [て] + て-form (来てて). This is the て-form of <a href=\"https://bunpro.jp/grammar_points/27\">ている form</a> - 来ていて with い omitted.",
                "structure": "V(る1) → 見<strong>る</strong> → 見<strong>て</strong>,<br> V(る5) → 座<strong>る</strong> →座<strong>って</strong>,  V(う) → 歌<strong>う</strong> → 歌<strong>って</strong>,V(つ) → 打<strong>つ</strong> → 打<strong>って</strong>, <br>V(く) → 歩<strong>く</strong> → 歩<strong>いて</strong>, V(ぐ) → 泳<strong>ぐ</strong> → 泳<strong>いで</strong>, <br>V(ぬ) → 死<strong>ぬ</strong> → 死<strong>んで</strong>,  V(ぶ) → 飛<strong>ぶ</strong> → 飛<strong>んで</strong>,  V(む) → 休<strong>む</strong> → 休<strong>んで</strong>, <br> V(す) → 話<strong>す</strong> → 話<strong>して</strong>, <br> <span class='chui'>⚠️</span>Irregular Verbs<span class='chui'>⚠️</span>,  <br>する→<span class='chui'>して</span>,  くる→<span class='chui'>きて</span>,  行く→<span class='chui'>行って</span>, <ruby>問<rt>と</rt></ruby>う→<span class='chui'><ruby>問<rt>と</rt></ruby>うて</chui>, <ruby>請<rt>こ</rt></ruby>う→<span class='chui'><ruby>請<rt>こ</rt></ruby>うて</chui>",
                "level": "JLPT5",
                "lesson-id": 4,
                "nuance": "[Notice the similarity to the <a href=\"https://bunpro.jp/grammar_points/41\">Verb[た]</a>.]",
                "incomplete": false,
                "grammar-order": 38
            }
        }
    ]
}
```

Personal note: Dealing with the raw html in the JSON API will be painful

The response has a weak ETag validator which enables to only get new content (with 304 Unmodified
otherwise).


GET api/v4/example_sentences
----------------------------

Information about the example sentences.

```
curl -H "Authorization: Bearer {USER_API_TOKEN}" "https://bunpro.jp/api/v4/example_sentences"
```

Example response
```
{
    "data": [
        {
            "id": "9",
            "type": "example-sentences",
            "attributes": {
                "grammar-point-id": 3,
                "japanese": "私（わたし）はトムです。",
                "english": "I <strong>(as the topic of this sentence)</strong> am Tom.",
                "nuance": null,
                "sentence-order": 0,
                "audio-link": "私はトムです。.mp3"
            }
        },
        [...]
        {
            "id": "17",
            "type": "example-sentences",
            "attributes": {
                "grammar-point-id": 5,
                "japanese": "これは寿司（すし）です。",
                "english": "<strong>This</strong> is sushi.",
                "nuance": null,
                "sentence-order": 2,
                "audio-link": "これは寿司です。.mp3"
            }
        }
    ]
}
```

Personal note: This query is 4MiB. This is really heavy and is likely to fail with a default request timeout of 10s.

GET api/v4/supplemental_links
-----------------------------

Information about the supplemental links.

```
curl -H "Authorization: Bearer {USER_API_TOKEN}" "https://bunpro.jp/api/v4/supplemental_links"
```

Example response
```
{
    "data": [
        {
            "id": "2",
            "type": "supplemental-links",
            "attributes": {
                "grammar-point-id": 1,
                "site": "Imabi",
                "link": "http://www.imabi.net/copularsentencesi.htm",
                "description": "Plain Speech: Stop at Past Tense 「だった」"
            }
        },
        [...]
        {
            "id": "1998",
            "type": "supplemental-links",
            "attributes": {
                "grammar-point-id": 631,
                "site": "iTalki",
                "link": "https://www.italki.com/article/1181/language-as-a-window-into-culture-mastering-the-nuances-of-part-1",
                "description": "Excellent breakdown, explanation, and examples"
            }
        }
    ]
}
```

Reviews API
-----------

TODO

`api/v3/reviews/create/{ID}?complete=true`

`api/v3/reviews/edit/{ID}?remove_review=true`

`api/v3/reviews/edit/{ID}?reset=true`
