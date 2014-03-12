clear
clc
NBCtraining = load('dataOutputFile.tsv');
display('training data loaded completely...')
NBClabel = load('labelOutputFile.tsv');
display('label training data loaded completely...')

train = spconvert(NBCtraining);
A = full(train);
display('training matrix converted!!!')
groups = NBClabel;         
k=10;
cvFolds = crossvalind('Kfold', groups, k);   %# get indices of 10-fold CV
cp = classperf(groups);                      %# init performance tracker

for i = 1:k                                  %# for each fold
    testIdx = (cvFolds == i);                %# get indices of test instances
    trainIdx = ~testIdx;                     %# get indices training instances

    %# train an SVM model over training instances
    svmModel = svmtrain(A(trainIdx,:), groups(trainIdx));

    %# test using test instances
    pred = svmclassify(svmModel, A(testIdx,:));

    %# evaluate and update performance object
    cp = classperf(cp, pred, testIdx);
end

%# get accuracy
cp.CorrectRate

%# get confusion matrix
%# columns:actual, rows:predicted, last-row: unclassified instances
cp.CountingMatrix

confMat = cp.CountingMatrix;
display('Confusion Matrix generated')

precision = (confMat(1,1)/(confMat(1,1) + confMat(2,1)))
recall = (confMat(1,1)/(confMat(1,1) + confMat(1,2)))
fmeasure = ((2 * precision * recall)/(precision + recall))