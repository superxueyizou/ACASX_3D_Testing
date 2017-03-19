%------------------------------------------------------%
%Deterministic Global Search to find high-accident-rate situations 
%for ACAS XU
%------------------------------------------------------%
clear all; clc;
%pwd: /home/xueyi/EclipseWorkSpace/Java/ACASX_3D_Testing/ACASX _3D_Testing
javaaddpath('./MatlabCode/ACASX_3D_Testing2.jar');
import ACASX_3D_Testing.*
%324185792, 54896327, 567672542, 588764357, 884185771

seeds = [588764357];
[row, col]=size(seeds);
for i =1:col
    clearvars -except seeds i
    seed = seeds(i);
    global AR;
    global testPoints;
    global count;
    testPoints=[];
    AR= search.AccidentRateEvaluator(seed)
    count=0;
    %search.AccidentRate.main('785945568')

    % 1. Establish bounds for variables
    bounds = [169, 304;
              -67, 58;
              20, 30;              
              0,   500;
              -180,180;
              -100,100;              
              169, 304;
              -180,180;
              -67, 58
              ];
    % 2. Send options to Direct
    %    We tell DIRECT that the globalmin = 0
    %    It will stop within 1% of solution
    options.testflag  = 1; 
    options.globalmin = 0; 
    options.showits   = 1;
    options.tol       = 1;
    
    %opts.maxevals  = 10000; %max. number of function evals     (default is 1000)
 	%opts.maxits    = 100; %max. number of iterations         (default is 10)
%     opts.maxdeep =100000;
    
    % 2a. NEW!
    % Pass Function as part of a Matlab Structure
    Problem.f = 'maxAccidentRate';

    % 3. Call DIRECT
    tic
    [fmin,xmin,hist] = Direct(Problem,bounds,options);
    disp(seed)
    toc

    

    % 4. Plot iteration statistics
%     figure
%     hold on
%     plot(hist(:,2),1.0-hist(:,3), '-*')
%     xlabel('simulations');
%     ylabel('accident rate');
%     title('Iteration Statistics for maxAccidentRate');
%     hold off
    
    filename = strcat('./DataSet/Experiment2/Direct/testPoints', num2str(seed), '.mat'); 
    save(filename, 'testPoints')
    filename = strcat('./DataSet/Experiment2/Direct/testPoints', num2str(seed), '.csv'); 
    csvwrite(filename,testPoints)
    filename = strcat('./DataSet/Experiment2/Direct/hist ', num2str(seed), '.mat'); 
    save(filename, 'hist')
    
end


