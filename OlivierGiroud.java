/*
 *  Copyright (C) 2021 Gabriele Crestanello, Pietro Volpe
 *  
 *  This file is part of the MNKGame v2.0 software developed for the
 *  students of the course "Algoritmi e Strutture di Dati" first 
 *  cycle degree/bachelor in Computer Science, University of Bologna
 *  A.Y. 2020-2021.
 *
 *  This  is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this file.  If not, see <https://www.gnu.org/licenses/>.
 */
package mnkgame;

import java.util.Random;

/**
 * Software player for the MNK-Game.
 * <p>
 * It can make autonomous decisions, based on an Alpha-Beta pruning exploration
 * tree, which depth is determined based on the number of remaining free cells.
 * In case it exceedes the maximum time to make a decision, it picks the best
 * one until that moment.
 * </p>
 */
public class OlivierGiroud implements MNKPlayer {

    private Random rand;
    private MNKBoard B;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private int TIMEOUT;
    private long start;

    // VARIABILI ACCUMULATRICI PER TENERE TRACCIA DEL NUMERO DI VISITE FATTE
    // nodes: nodi/generazioni generiche di sottotabelle di gioco nell'albero AB pruning / terminals: nodi terminali (depth raggiunta
    // o gameState != OPEN) / starters: nodi generati direttamente nella funzione selectCell, prima di invocare AB pruning
    private double visitedNodes = 0, visitedTerminals = 0, visitedStarters = 0;

    // VARIABILI DI CONTROLLO PER SETTARE I VARI LIMITI 
    private double timeThreshold = 0.95;     //percentuale per controllare il limite del tempo di timeout, se lo sfora sceglie la mossa più valida fino a quel punto
    private double powLimit;      //numero massimo di nodi visitabili per prevenire overtime

    /**
     * Default empty constructor
     */
    public OlivierGiroud() {
    }

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        rand = new Random(System.currentTimeMillis());
        B = new MNKBoard(M, N, K);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        TIMEOUT = timeout_in_secs;
        powLimit = timeout_in_secs * Math.pow(10, 9);
    }

    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        visitedNodes = 0;
        visitedTerminals = 0;
        visitedStarters = 0;
        start = System.currentTimeMillis();

        //recupero l'ultima cella marcata dall'avversario (se è stata fatta almeno una mossa)
        if (MC.length > 0) {
            MNKCell c = MC[MC.length - 1];
            B.markCell(c.i, c.j);
        }

        //se c'è solo una mossa possibile la restituisco subito
        if (FC.length == 1) {
            B.markCell(FC[0].i, FC[0].j);
            return FC[0];
        }
        //controllo la presenza di mosse in grado di far vincere il bot/l'avversario al prossimo turno, reagisco di conseguenza
        //controllo se il bot può vincere con una mossa
        for (MNKCell d : FC) {
            if (B.markCell(d.i, d.j) == myWin) {
                //DEBUG OUTPUT
                //System.out.println("HO VINTO");
                return d;
            } else {
                B.unmarkCell();
            }
        }
        //controllo se l'avversario può vincere alla prossima mossa
        B.markCell(FC[0].i, FC[0].j);   //segno la mossa del bot
        for (MNKCell d : FC) {
            if (d.i != FC[0].i && d.j != FC[0].j) {
                if (B.markCell(d.i, d.j) == yourWin) {
                    B.unmarkCell();     //annullo la mossa dell'avversario
                    B.unmarkCell();     //annullo la mia mossa
                    B.markCell(d.i, d.j);   //segno la mossa per bloccare l'avversario
                    //DEBUG OUTPUT
                    //System.out.println("HO BLOCCATO UNA MOSSA VINCENTE");
                    return d;
                } else {
                    B.unmarkCell();
                }
            }
        }
        B.unmarkCell();     //annullo la mossa iniziale del bot
        //controllo se l'avversario può vincere in posizione FC[0], visto che con lo scorso controllo usavo la "mossa segnaposto" del bot in quella posizione
        if (FC.length > 1) {
            B.markCell(FC[1].i, FC[1].j);   //segno la mossa del bot
            MNKCell d = FC[0];
            if (B.markCell(d.i, d.j) == yourWin) {
                B.unmarkCell();     //annullo la mossa dell'avversario
                B.unmarkCell();     //annullo la mia mossa
                B.markCell(d.i, d.j);   //segno la mossa per bloccare l'avversario
                //DEBUG OUTPUT
                //System.out.println("HO BLOCCATO UNA MOSSA VINCENTE");
                return d;
            } else {
                B.unmarkCell();
            }
            B.unmarkCell();     //annullo la mossa iniziale del bot
        }

        //altrimenti applico l'algoritmo per scegliere la mossa migliore
        int maxDepth = GetMaxDepth(FC.length);      //determino la profondità massima a cui posso arrivare per ridurre al minimo i timeout

        int move = 0, bestScore = Integer.MIN_VALUE, score = bestScore;

        for (int i = 0; i < B.getFreeCells().length; i++) {
            visitedStarters++;
            visitedNodes++;
            if ((System.currentTimeMillis() - start) / 1000.0 > TIMEOUT * timeThreshold) { //se ho sforato il limite del tempo di timeout
                //DEBUG OUTPUT
                //System.out.println("OVERTIME EVITATO");
                break;
            } else {
                MNKCell c = FC[i];
                B.markCell(c.i, c.j);
                score = AlphaBeta(B, false, 1, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                B.unmarkCell();
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        //DEBUG OUTPUT
        //System.out.println("\nScelta migliore: " + move + " , score: " + bestScore + " // riga: " + FC[move].i + " , colonna: " + FC[move].j + " // Profondità: " + maxDepth + " , Starters: " + visitedStarters + ", Terminals: " + visitedTerminals + ", Nodes:" + visitedNodes + "\n-----------------------------------------\n");

        B.markCell(FC[move].i, FC[move].j);
        return FC[move];
    }

    public String playerName() {
        return "Olivier Giroud";
    }

    private int AlphaBeta(MNKBoard board, boolean isMaximizing, int depth, int maxDepth, int alpha, int beta) {
        if ((System.currentTimeMillis() - start) / 1000.0 > TIMEOUT * timeThreshold) { //se ho sforato il limite del tempo di timeout
            return 0;
        }

        visitedNodes++;

        int eval;
        //valutazione in base al gamestate e alla profondità (nodi terminali o profondità di esplorazione raggiunta)
        if (!board.gameState().equals(MNKGameState.OPEN) || depth >= maxDepth) {
            return Evaluate(board.gameState(), depth, maxDepth);
        } else if (isMaximizing) {    //maximizing
            eval = Integer.MIN_VALUE;
            MNKCell[] c = board.getFreeCells();
            for (MNKCell cl : c) {
                board.markCell(cl.i, cl.j);
                eval = Integer.max(eval, AlphaBeta(board, !isMaximizing, depth + 1, maxDepth, alpha, beta));
                alpha = Integer.max(alpha, eval);
                board.unmarkCell();
                if (beta <= alpha) {    //taglio
                    return eval;
                }
            }
            return eval;
        } else {                    //minimizing
            eval = Integer.MAX_VALUE;
            MNKCell[] c = board.getFreeCells();
            for (MNKCell cl : c) {
                board.markCell(cl.i, cl.j);
                eval = Integer.min(eval, AlphaBeta(board, !isMaximizing, depth + 1, maxDepth, alpha, beta));
                beta = Integer.min(beta, eval);
                board.unmarkCell();
                if (beta <= alpha) {    //taglio
                    return eval;
                }
            }
            return eval;
        }
    }

    private int Evaluate(MNKGameState state, int depth, int maxDepth) {
        visitedTerminals++;

        int ret;
        if (state.equals(myWin)) {              //vittoria bot
            ret = 100 / depth;
        } else if (state.equals(yourWin)) {    //vittoria avversario
            ret = -100 / depth;
        } else if (state.equals(MNKGameState.DRAW)) {        //pareggio
            ret = 0;
        } else {     // profondità di esplorazione raggiunta
            ret = 0;
            //DEBUG OUTPUT
            //System.out.println("PROFONDITA' RAGGIUNTA");
        }
        return ret;
    }

    private int GetMaxDepth(int len) {
        long n = len, counter = n;
        int ret = 1;
        do {
            n = n - 1;
            if (counter * n <= powLimit) {
                ret++;
            }
            counter = counter * n;
        } while (counter < powLimit && n > 1);
        return ret;
    }

}
