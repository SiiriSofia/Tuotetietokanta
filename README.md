# Ohj2 harjoitustyö: Tuotetietokanta
JYU:n Ohjelmointi 2 -kurssin harjoitustyönä tehty tietokantasovellus, jonka tarkoituksena on
toimia kaupan tuotevalikoiman hallinnassa ja pitää kirjaa siitä, kuka työntekijä vastaa mistäkin tuoteosastosta 
tuotteineen.

Tuotetietokannassa on määritetty etukäteen tietty määrä osastoja, joiden puitteissa 
sovelluksen käyttöliittymässä voidaan lisätä, muokata ja poistaa tuotteita ja niiden tarkempia tietoja 
(tuotteen nimi, hinta, maksimimäärä varastossa, tuoteosasto jolla sijaitsee) erillisessä syöttöikkunassa.
Tuotetietokannan graafisen käyttöliittymän näkymä listaa ja näyttää yksittäisiä tuotteita sekä niiden sijainnin 
(tuoteosaston) kaupassa. Tuoteosaston tietojen yhteydessä näkyy myös kyseisestä tuoteosastoista 
vastuussa olevien työntekijöiden tiedot (nimi, puhelin). Käyttöliittymässä voidaan lisätä, muokata ja poistaa 
tuoteosastoista vastuussa olevien henkilöiden tietoja. Tietojen syöttönäkymät sisältävät oikeellisuustarkistuksia
tietyissä kentissä. Graafinen käyttöliittymä sisältää tuotteiden hakutoiminnon, jossa valittuna olevan
hakukriteerin perusteella tuotteita voi hakea, rajata tai listata tuoteosastoittain tai tuotenimen perusteella. Käyttöliittymän
menupalkki sisältää toiminnot tietokantaan tallentamiseen, ohjelman sulkemiseen ja tulostamiseen. Tulostusnäkymässä
näkyy raporttimuodossa kaikkien niiden tuotteiden tiedot, jotka aktiivisena oleva hakuehto on rajannut.


Kielenä Java, graafinen käyttöliittymä toteutettu JavaFX-kirjastolla. 
Kaupan tuotteet, tuoteosastot ja henkilöt erillisessä tietokannassa, josta sovellus hakee tietoja SQL-kyselyillä.
