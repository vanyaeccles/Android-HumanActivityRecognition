package com.fitreo.wisdmtestapp;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by vanya on 20-Apr-18.
 *
 * This class obtains and processes data from the accelerometer
 */

public class AccelerometerDataProcessor {

    Context context;

    //
    int QUEUE_SIZE = 200;
    int QUEUE_SIZE_3_AXIS = QUEUE_SIZE * 3;
    // maybe put these return values in a separate object
    float[] inputFloats_x;
    float[] inputFloats_h_feat;

    //Statistical values
    float meanX;
    float meanY;
    float meanZ;
    float stdX;
    float stdY;
    float stdZ;
    ArrayList<Float> diffFromMeanX;
    ArrayList<Float> diffFromMeanY;
    ArrayList<Float> diffFromMeanZ;
    ArrayList<Float> absDiffFromMeanX;
    ArrayList<Float> absDiffFromMeanY;
    ArrayList<Float> absDiffFromMeanZ;
    ArrayList<Float> squaredDiffX;
    ArrayList<Float> squaredDiffY;
    ArrayList<Float> squaredDiffZ;
    float meanAbsDifferenceFromMeanX;
    float meanAbsDifferenceFromMeanY;
    float meanAbsDifferenceFromMeanZ;
    float meanSqrtOfPointwiseSquares;
    ArrayList<Float> histX;
    ArrayList<Float> histY;
    ArrayList<Float> histZ;

    //float[] feature_means = {0.46475181213519784f, 7.16442150807658f, 0.4377369509976924f, 4.713396983846886f, 5.142538122709364f, 3.978876146328213f, 3.845244563594393f, 4.252912474548665f, 3.0062956033663584f, 11.931427175240962f, 10.773992127053075f, 13.049273788516357f, 20.49450251119859f, 28.326727297407356f, 33.55707886520972f, 32.034613818379256f, 25.788244875797474f, 17.147685625084836f, 10.10234831003122f, 8.725532781322112f, 9.271888149857473f, 13.54228315460839f, 20.126238631736122f, 27.611918012759602f, 31.377222750101804f, 27.649925342744673f, 22.08022261436134f, 17.519478756617346f, 13.303379937559386f, 17.517442649653862f, 7.2865481199945705f, 18.54974888014117f, 31.82163702999864f, 37.579476041808064f, 35.94488937152165f, 26.172661870503596f, 18.16668929007737f, 12.037600108592372f, 7.4042351024840505f, 5.036514184878512f};
    float[] feature_means = {1.2334214735624598f, 7.41294096166555f, 0.28674226553205545f, 4.249025809649697f, 4.983751252478503f, 3.4326008195637865f, 3.4908178354263084f, 4.101308516192995f, 2.5858711136814234f, 11.475720423000661f, 9.121612690019829f, 13.001982815598149f, 19.548909451421018f, 27.882022471910112f, 33.062128222075344f, 32.911434236615996f, 27.12557832121613f, 18.216126900198283f, 10.86582947785856f, 8.264375413086583f, 8.929940515532056f, 13.593853271645736f, 17.83807005948447f, 26.532716457369464f, 31.92927957699934f, 29.30568407138136f, 23.271315267680105f, 18.63284864507601f, 13.126239259748843f, 16.840052875082616f, 7.671183079973562f, 19.66226040978189f, 34.486120290812956f, 39.54296100462657f, 35.099140779907465f, 26.328155981493722f, 16.375743555849304f, 10.131196298744216f, 6.124256444150694f, 4.5789821546596166f};
    //float[] feature_stds = {4.582864107908967f, 3.689992817688869f, 2.126933237976914f, 2.7491438939602686f, 2.636815618775763f, 1.9438079034753677f, 2.3774812580210822f, 2.317644512540503f, 1.5818963570519728f, 1.7924458529709382f, 12.89373877686104f, 10.464332522420236f, 13.801455923378322f, 16.354630750313945f, 16.431675685674087f, 16.165016151559243f, 15.612988009935119f, 12.554349433671055f, 8.64552232357024f, 10.911437028496918f, 9.527826464236027f, 9.705327041748129f, 14.404794790135128f, 17.210115189313534f, 17.830045037447096f, 15.929566012988088f, 13.243475941261638f, 11.748258449580128f, 8.992651524220955f, 17.704041378203733f, 7.195557059033352f, 19.65520462780703f, 23.487687370111907f, 21.437150164870193f, 20.416032716819384f, 19.074516041659553f, 16.333197938569068f, 12.962091855514334f, 8.786196924459764f, 4.2883570831595375f};
    float[] feature_stds =  {3.904837534117901f, 3.613765228889272f, 2.4482624867278835f, 2.5994891790786316f, 2.6279327801207235f, 1.602295110710508f, 2.242718049067602f, 2.262759492656133f, 1.2533363628444967f, 1.3862556811219129f, 9.266296919597329f, 10.699617762275096f, 12.582224757242544f, 16.881971224352107f, 17.62370421888263f, 17.190471626188852f, 15.162637580574422f, 12.542915794120516f, 8.945740950456813f, 9.179391910461487f, 7.0831420512607455f, 10.10401694953422f, 13.75362301999691f, 17.14173701067467f, 16.949117758913342f, 16.71773205849901f, 15.09787686107682f, 12.578571236190252f, 9.338068837343393f, 17.032089242839724f, 7.869218251546589f, 20.487005458176583f, 21.972523226759822f, 20.543176808222672f, 19.42242911511502f, 19.382018406546106f, 15.560382562076953f, 10.535856302812267f, 6.311355275382208f, 3.63101602190086f};


    public AccelerometerHandler accelerometerHandler;
    public LimitedSizeQueue<Float> dataQueueX;
    public LimitedSizeQueue<Float> dataQueueY;
    public LimitedSizeQueue<Float> dataQueueZ;


    //constructor
    AccelerometerDataProcessor(Context context) {
        this.context = context;
        accelerometerHandler = new AccelerometerHandler(context);

        dataQueueX = accelerometerHandler.dataQueueX;
        dataQueueY = accelerometerHandler.dataQueueY;
        dataQueueZ = accelerometerHandler.dataQueueZ;

        diffFromMeanX = new ArrayList<>();
        diffFromMeanY = new ArrayList<>();
        diffFromMeanZ = new ArrayList<>();
        absDiffFromMeanX = new ArrayList<>();
        absDiffFromMeanY = new ArrayList<>();
        absDiffFromMeanZ = new ArrayList<>();
        squaredDiffX = new ArrayList<>();
        squaredDiffY = new ArrayList<>();
        squaredDiffZ = new ArrayList<>();


        histX = new ArrayList<>();
        histY = new ArrayList<>();
        histZ = new ArrayList<>();

    }

    public boolean isQueueFilled() {
        // only need to check one of the queues
        return dataQueueZ.isQueueFilled;
    }


    public void onReadyToProcessNewAccValues() {
        // preprocess the values for the CNN

        //check that there are the correct number of values in the queue @TODO
        //if (!isQueueFilled())
        //    return;

        // get statistical features
        meanX = arithmeticMean(dataQueueX);
        meanY = arithmeticMean(dataQueueY);
        meanZ = arithmeticMean(dataQueueZ);

        // this could be microoptimized but maybe not worth it
        diffFromMeanX = getDifferencesFromMean(dataQueueX, meanX);
        diffFromMeanY = getDifferencesFromMean(dataQueueY, meanX);
        diffFromMeanZ = getDifferencesFromMean(dataQueueZ, meanX);
        absDiffFromMeanX = getAbsDifferencesFromMean(diffFromMeanX, meanX);
        absDiffFromMeanY = getAbsDifferencesFromMean(diffFromMeanY, meanY);
        absDiffFromMeanZ = getAbsDifferencesFromMean(diffFromMeanZ, meanZ);
        squaredDiffX = getSquaredDifferencesFromMean(absDiffFromMeanX, meanX);
        squaredDiffY = getSquaredDifferencesFromMean(absDiffFromMeanY, meanY);
        squaredDiffZ = getSquaredDifferencesFromMean(absDiffFromMeanZ, meanZ);
        meanAbsDifferenceFromMeanX = arithmeticMean(absDiffFromMeanX);
        meanAbsDifferenceFromMeanY = arithmeticMean(absDiffFromMeanY);
        meanAbsDifferenceFromMeanZ = arithmeticMean(absDiffFromMeanZ);

        //histX = makeHist(dataQueueX);
        //ArrayList<Float> testArrayList = new ArrayList<Float>();
        //testArrayList.addAll(Arrays.asList(3.0f, 67.0f, 34.0f, 67.0f, 59.0f, 80.0f, 71.0f, 33.0f, 10.0f, 89.0f, 44.0f, 63.0f, 54.0f, 80.0f, 10.0f, 40.0f, 10.0f, 2.0f, 9.0f, 5.0f, 10.0f));

        //histX = makeHist(testArrayList, 10);
        histX = makeHist(dataQueueX, 10);
        histY = makeHist(dataQueueY, 10);
        histZ = makeHist(dataQueueZ, 10);

        stdX = standardDeviation(squaredDiffX);
        stdY = standardDeviation(squaredDiffY);
        stdZ = standardDeviation(squaredDiffZ);

        meanSqrtOfPointwiseSquares = getMeanSqrtOfPointwiseSquares(dataQueueX, dataQueueY, dataQueueZ);

        // perform mean centering
        inputFloats_x = meanCentering(dataQueueX, dataQueueY, dataQueueZ);

    }

    public float[] getStatisticalFeatures(){

        ArrayList<Float> statFeatures = new ArrayList<>();
        //means
        statFeatures.add(meanX);
        statFeatures.add(meanY);
        statFeatures.add(meanZ);


        //stds
        statFeatures.add(stdX);
        statFeatures.add(stdY);
        statFeatures.add(stdZ);
        //mean abs difference from mean
        statFeatures.add(meanAbsDifferenceFromMeanX);
        statFeatures.add(meanAbsDifferenceFromMeanY);
        statFeatures.add(meanAbsDifferenceFromMeanZ);
        //mean pointwise squares
        statFeatures.add(meanSqrtOfPointwiseSquares);

        //hist features
        for(int i = 0; i < histX.size(); i++){
            statFeatures.add(histX.get(i));
        }
        for(int i = 0; i < histY.size(); i++){
            statFeatures.add(histY.get(i));
        }
        for(int i = 0; i < histZ.size(); i++){
            statFeatures.add(histZ.get(i));
        }




        //convert to float array
        float[] statFeaturesArray = new float[40];
        for(int j = 0; j < statFeatures.size(); j++){
            //negate by mean, divide by std (from ignatov code, array is saved from training feature data)
            statFeatures.set(j, statFeatures.get(j) - feature_means[j]);
            statFeatures.set(j, statFeatures.get(j) / feature_stds[j]);
            statFeaturesArray[j] = statFeatures.get(j);
        }
        Log.d("h_feat", "getStatisticalFeatures (arraylist): "  + statFeatures);
        return statFeaturesArray;
    }



    private float[] meanCentering(ArrayList<Float> xvalues, ArrayList<Float> yvalues, ArrayList<Float> zvalues) {
        //takes an arraylist of values (difference from mean) and converts into a float array
        float[] centered_data = new float[QUEUE_SIZE_3_AXIS];
        ArrayList<Float> cd_copy = new ArrayList<>();
        // add x - xmean, y - ymean and z - zmean
        for(int i = 0; i < QUEUE_SIZE; i++) {
            centered_data[i] = xvalues.get(i) - meanX;
            centered_data[i + QUEUE_SIZE] = yvalues.get(i) - meanY;
            centered_data[i + QUEUE_SIZE*2] = zvalues.get(i) - meanZ;
        }
        for(int i = 0; i < QUEUE_SIZE*3; i++) {
            cd_copy.add(centered_data[i]);
        }

        Log.d("centered_data", "meanCentering(arraylist): "  + cd_copy);
        return centered_data;
    }


    private float arithmeticMean(ArrayList<Float> values) {
        Float sum = 0.0f;
        //if (!values.isEmpty()) {
            for (Float value : values) {
                sum += value;
            }
            return sum / values.size();
        //}
        //return sum;
    }

    private float standardDeviation(ArrayList<Float> squaredDiff) {
        //if (!squaredDiff.isEmpty()) {
            float std = (float)Math.sqrt(arithmeticMean(squaredDiff));
            return std;
        //}
        //return 0;
    }

    private ArrayList<Float> getSquaredDifferencesFromMean(ArrayList<Float> values, float mean) {
        // populates the diffSquaredArray with the squared differences from the mean
        ArrayList<Float> diffSquaredArray = new ArrayList<>();
        //if (!values.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                float diff = values.get(i) - mean;
                diffSquaredArray.add(diff * diff);
            }
        //}
        return diffSquaredArray;
    }

    private ArrayList<Float> getDifferencesFromMean(ArrayList<Float> values, float mean) {
        // populates the diffArray with the differences from the mean
        ArrayList<Float> diffArray = new ArrayList<>();

        //if (!values.isEmpty()) {
        for (int i = 0; i < values.size(); i++) {
            float diff = values.get(i) - mean;
            diffArray.add(diff);
        }
        //}
        return diffArray;
    }

    private ArrayList<Float> getAbsDifferencesFromMean(ArrayList<Float> values, float mean) {
        // populates the absDiffArray with the absolute diff from mean
        ArrayList<Float> absDiffArray = new ArrayList<>();
        //if (!values.isEmpty()) {
        for (int i = 0; i < values.size(); i++) {
            float diff = values.get(i) - mean;
            absDiffArray.add(Math.abs(diff));
        }
        //}
        return absDiffArray;
    }




    private float getMeanSqrtOfPointwiseSquares(ArrayList<Float> xValues, ArrayList<Float> yValues, ArrayList<Float> zValues) {
        // gets mean(sqrt(x.^2 + y.^2 + z.^2))
        ArrayList<Float> pointwiseSquares = new ArrayList<>();
        for (int i = 0; i < xValues.size(); i++) {
            float xVal = xValues.get(i);
            float yVal = yValues.get(i);
            float zVal = zValues.get(i);
            pointwiseSquares.add((float) Math.sqrt(xVal * xVal + yVal * yVal + zVal * zVal));
        }
        return arithmeticMean(pointwiseSquares);
    }


    private ArrayList<Float> makeHist(ArrayList<Float> values, int _numberOfBins) {
        // perform matlab code - hist(x, 10)/n
        // divide values into ten evenly spaced sorted bins by count and divide by the list size
        // @todo this does not work exactly like the matlab implementation


        //sort the list
        ArrayList<Float> sortedList = new ArrayList<>(values);
        Collections.sort(sortedList);

        //group into ten bins
        ArrayList<Float> binnedList = new ArrayList<>();
        int listSize = sortedList.size();
        int numberOfBins = _numberOfBins;
        float minVal = sortedList.get(0);
        float maxVal = sortedList.get(listSize - 1);
        float incrementVal = (maxVal - minVal) / numberOfBins;
        float currentBinLimit = minVal + incrementVal;
        float summedBin = 0;

        Log.d("testhist", "increment value " + incrementVal);
        Log.d("testhist", "sorted list " + sortedList);

        int addedIndex = -1;

        for(int i =0; i < numberOfBins; i ++){
            for(int j = 0; j < listSize; j++){
                //Log.d("testhist", "current value" + sortedList.get(j));
                //Log.d("testhist", "current bin limit" + currentBinLimit);

                if(j > addedIndex){
                    if(sortedList.get(j) <= currentBinLimit){
                        summedBin += 1;
                        //Log.d("testhist", "value " +  sortedList.get(j) + " less than " + currentBinLimit);
                        addedIndex = j;
                    }
                    else{
                        break;
                    }
                }
            }
            // divide by the listSize and add to the binnedList
            //summedBin /= listSize;
            binnedList.add(summedBin);
            //move onto the next bin
            currentBinLimit += incrementVal;
            summedBin = 0;
        }



        Log.d("testhist", "Hist = " + binnedList);
        return binnedList;
    }

}


