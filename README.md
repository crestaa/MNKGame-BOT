# MNKGame-BOT
Giocatore software per il tris generalizzato (MNK-Game)

<img src="https://www.unibo.it/it/logo-unibo.png/@@images/44d79f14-1a99-4a5d-997f-90df029bd63e.png" alt="UniBO" width="40%"/>

<a href="https://www.gabrielecrestanello.it" target="_blank">Crestanello Gabriele</a>

<a href="https://github.com/3amfox" target="_blank">Volpe Pietro</a>

## Introduzione

Il progetto si basa sulla realizzazione di un giocatore software per l'MNK-Game, sfruttando l'algoritmo di alpha-beta pruning. Le specifiche seguite sono quelle richieste dalla consegna per il <a href="https://www.unibo.it/it/didattica/insegnamenti/insegnamento/2020/350957" target="_blank">corso di algoritmi e strutture di dati (A.A. 2020/2021)</a>. 

## Utilizzo / Comandi
### Compilazione
```cmd
  javac -cp ".." *.java
```
### Esecuzione
Human vs Computer
```cmd
  java -cp ".." mnkgame.MNKGame 3 3 3 mnkgame.OlivierGiroud
```
Computer vs Computer (output del solo risultato)
```cmd
  java -cp ".." mnkgame.MNKPlayerTester 5 5 4 mnkgame.OlivierGiroud mnkgame.QuasiRandomPlayer
```
Computer vs Computer (output delle mosse e del risultato)
```cmd
  java -cp ".." mnkgame.MNKPlayerTester 5 5 4 mnkgame.OlivierGiroud mnkgame.QuasiRandomPlayer -v
```
Computer vs Computer (output di mosse e risultato, timeout personalizzato di 1 secondo, 10 ripetizioni della partita)
```cmd
  java -cp ".." mnkgame.MNKPlayerTester 3 3 3 mnkgame.OlivierGiroud mnkgame.QuasiRandomPlayer -v -t 1 -r 10
```

## Licenza
MIT License

Copyright (c) 2021 Crestanello Gabriele, Volpe Pietro

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
