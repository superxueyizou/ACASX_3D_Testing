function value = maxAccidentRate(x)
    global testPoints;
    global AR;
    global count;
    accidentRate = AR.value(x);
    value =1.0-accidentRate;  
    testPoints=[testPoints; x', accidentRate];
    count = count +1;

