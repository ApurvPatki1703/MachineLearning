%%This is a logistic regression classifier. This classifier uses the
%%coeff.model to classify text.

function pred = LRClassifier(coeff, instances)
    d = size(instances);
    pred = zeros( d(1) , 1);
    for i = 1 : d(1)
        pihat = mnrval(coeff,instances(i ,:));
        if(pihat(1) > pihat(2))
            label = 1;
        end
        if(pihat(2) >= pihat(1))
            label = 2;
        end
        pred(i) = label;
    end
end