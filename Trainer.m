%%This function is used to compute training model of logistic regression
%%It dumps the model into file

function coeff = Trainer(train, labels)
    %%convert labels to nominal values.
    labeldata = nominal(labels);
    %%convert to double.
    labeldata = double(labeldata);
    %%fit model to training data.
    coeff = mnrfit(train,labeldata);
    %%dump model to external file.
    %%dlmwrite('coeff.model', coeff);
end