import Jama.Matrix;
import Jama.SingularValueDecomposition;
import java.util.Random;

/**
 * Created by andreahe on 7/1/15.
 * Implements Van Vu's SVD I algorithm for graph clustering.
 * We make the following assumptions: clusters area all the same size, interior/exterior edge probabilities
 * are the same regardless of the cluster (i.e, the "p-q model").
 * Main method generates a random graph subject to the paramaters and uses SVD I to find an appropriate geometric representation.
 */
public class SVDI {
    static int numClusters = 3;
    static int clusterSize = 5;
    static int numVertices = numClusters * clusterSize;
    static double interiorProb = .9;
    static double exteriorProb = .1;
    static double[][] P = new double[numVertices][numVertices]; //Probability matrix
    static double[][] P_hat = new double[numVertices][numVertices]; //Adjacency matrix generated by P
    static Matrix projectedPoints; //Projection of P_hat onto P_k_hat

    public static void main(String[] args) {
        Random r = new Random();
        //Initialize P
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if ((i - i % clusterSize) == (j - j % clusterSize)) {
                    P[i][j] = interiorProb;
                } else {
                    P[i][j] = exteriorProb;
                }
            }
        }
        //Initialize P_hat
        for (int i = 0; i < numVertices; i++) {
            for (int j = i; j < numVertices; j++) {
                double rand = r.nextDouble();
                if (rand < P[i][j]) {
                    P_hat[i][j] = 1;
                    P_hat[j][i] = 1;
                } else {
                    P_hat[i][j] = 0;
                    P_hat[j][i] = 0;
                }
            }
        }
        Matrix P_hat_matrix = new Matrix(P_hat); //Convert P_hat from double[][] to Matrix
        SingularValueDecomposition SVD = new SingularValueDecomposition(P_hat_matrix);
        Matrix leftSingVects = SVD.getU();
        Matrix P_k_hatMatrix = leftSingVects.getMatrix(0, numVertices - 1, 0, numClusters - 1);
        Matrix projection = (P_k_hatMatrix.times((P_k_hatMatrix.transpose().times(P_k_hatMatrix)).inverse())).times(P_k_hatMatrix.transpose());
        projectedPoints = projection.times(P_hat_matrix);
        projectedPoints.print(3,3);
    }

    //Input int a is the index of a vertex. Returns a double[] containing distances from each vertex to a.
    public static double[] distFromPointA(int a) {
        double[] distFromPointA = new double[numVertices];
        double d = 0;
        for (int i = 0; i < numVertices; i++) {
            d = 0;
            for (int j = 0; j < numVertices; j++) {
                d += Math.pow(projectedPoints.get(j, a) - projectedPoints.get(j, i), 2);
            }
            distFromPointA[i] = d;
            System.out.println(d);
        }
        return distFromPointA;
    }
}
