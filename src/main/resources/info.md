## Game files:
___

Converted ```items_game.json``` contains 
every in-game item, including prefabs, rarities and colors, but most important are the following:
> * ```items_game```: top level key
>   * ```item_sets```: list of containers
>   * ```music_definitions```: list of music kits
>   * ```revolving_loot_lists```: all types of containers?
>   * ```paint_kits```: skins
>   * ```sticker_kits```: stickers

Converted ```csgo_english.json``` is basically a ```Map<uid: translation>``` (uid's are located in ```items_game.json```
file as ```name``` field of some entity). There are several csgo_language.txt files that have the same
mappings.
___

## Steam Community Market:
___
> #### [Search on market](https://steamcommunity.com/market/search/render/)
> Request market positions with >0 listings.
> 
> ```https://steamcommunity.com/market/search/render/[options]```
> 
> Use ```?``` before options
> 
> Use ```&``` as option separator
> 
> **Options:**
> * ```norender```: use server-side html rendering, eg ```1 - don't use render```
> * ```appid```: id of game, eg ```730 - CS:GO```
> * ```sort_column```: sort by column
> * ```sort_dir```: sort order, eg ```desc - descending order```
> * ```count```: count of positions (only in range ```1 <= x <= 100```)
> * ```start```: skip x first positions, if x > positions - will skip everything
> * ```search_descriptions```: no idea how it works 

> #### [Icon](https://community.cloudflare.steamstatic.com/economy/image/)
> Request Icon by ```icon_url``` with ```res``` resolution.
> 
> ```https://community.cloudflare.steamstatic.com/economy/image/[icon_url]/[res]fx[res]f```
___