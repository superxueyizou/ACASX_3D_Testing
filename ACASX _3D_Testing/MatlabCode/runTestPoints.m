clear all; clc;
javaaddpath('/home/xueyi/Desktop/ACASX_3D_Testing.jar');
import ACASX_3D_Testing.*
global AR;
load testPoints898946497
AR= search.AccidentRateEvaluator(898946497)
[rows, cols] = size(testPoints);
tic
for i=1:rows
    x=testPoints(i,1:end-1);
    accidentRate = AR.value(x);
end
toc
%344, 392, 417