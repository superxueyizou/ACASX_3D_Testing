%------------------------------------------------------%
%Deterministic Global Search to find high-accident-rate situations 
%for ACAS XU
%------------------------------------------------------%
clear all; clc;
javaaddpath('/home/xueyi/Desktop/ACASX_3D_Testing.jar');
import ACASX_3D_Testing.*

seeds = [567672542, 898946497, 679463479,884185791, 588764257];
for i =1:5
    clearvars -except seeds i
    seed = seeds(i);
    global AR;
    global testPoints;
    testPoints=[];
    AR= search.AccidentRateEvaluator(seed)
    %search.AccidentRate.main('785945568')

    % 1. Establish bounds for variables
    bounds = [-67, 58;
              169, 304;
              -100,100;
              0,   500;
              -180,180;
              -67, 58;
              169, 304;
              -180,180;
              20, 30];
    % 2. Send options to Direct
    %    We tell DIRECT that the globalmin = 0
    %    It will stop within 2% of solution
    options.testflag  = 1; 
    options.globalmin = 0; 
    options.showits   = 1;
    options.tol       = 2;
    
%     opts.maxevals  = 100; %max. number of function evals     (default is 1000)
% 	opts.maxits    = 100; %max. number of iterations         (default is 10)
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
    figure
    hold on
    plot(hist(:,2),1.0-hist(:,3), '-*')
    xlabel('simulations');
    ylabel('accident rate');
    title('Iteration Statistics for maxAccidentRate');
    hold off
    
    filename = strcat('./ACASXGlobalSearch/testPoints', num2str(seed), '.mat'); 
    save(filename, 'testPoints')
    filename = strcat('./ACASXGlobalSearch/testPoints', num2str(seed), '.csv'); 
    csvwrite(filename,testPoints)
    filename = strcat('./ACASXGlobalSearch/hist ', num2str(seed), '.mat'); 
    save(filename, 'hist')
    
end


