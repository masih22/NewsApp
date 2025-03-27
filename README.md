# NewsApp

A simple news app that aggregates news from multiple sources. It can be used to view top headlines for broad categories, perform specific term searches, view news from specific sources, and view local news in different states.

The app contains:
- A Sources Activity that allows the user to filter their search by selecting a desired news source
- A Results Activity that displays news search results
- A Maps Activity that displays “local news” for the chosen state
- A Top Headlines Activity that display top headlines for a category the user chooses

The App leverages three APIs from [NewsAPI](https://newsapi.org/)
- [Everything Search](https://newsapi.org/docs/endpoints/everything) - to retrieve news articles for the user’s given search term or chosen location.
- [Top Headlines](https://newsapi.org/docs/endpoints/top-headlines) - to view trending articles across different categories.
- [Sources](https://newsapi.org/docs/endpoints/sources) - to pull news sources indexed by the NewsAPI.
