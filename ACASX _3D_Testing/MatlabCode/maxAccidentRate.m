function value = maxAccidentRate(x)
    global testPoints;
    global AR;
    accidentRate = AR.value(x);
    value =1.0-accidentRate;  
    testPoints=[testPoints; x', accidentRate];

