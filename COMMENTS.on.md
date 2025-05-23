Látszik, hogy sok energiát és időt pakoltál bele, és az is, hogy tudsz kódolni.
Ugyanakkor úgy tűnik, hogy elveszett a fókusz a megvalósítás közben:
* Vannak olyan feladatrészek pl. a conclude megvalósítás, amit nem találtunk.
* A service layer teljesen hiányzik pl. nincs ellenőrizve, hogy a 1o1-hoz egyáltalán hozzáférhet-e a user
* A megoldás nagy része a kontrollerekben lett implementálva.
* Vannak olyan megoldások, amiket nem értünk, hogy miért kerültek megvalósításra pl. conference vs meeting.

Másrészt a kód (szerintünk) nincs tekintettel arra, hogy ezt a jövőben mások is kalapálhatják pl. a változó nevekben "A", "B" szerepel vagy "_title", illetve úgy véljük, hogy ez a kód nehezen lenne karbantartható.
