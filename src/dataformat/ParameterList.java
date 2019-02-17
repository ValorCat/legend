package dataformat;

import execute.Environment;

import java.util.List;

/**
 * @since 2/17/2019
 */
public class ParameterList {

    private String name;
    private String[] params;
    private Expression[] bounds;

    public ParameterList(String name, List<Expression> params) {
        this.name = name;
        this.params = new String[params.size()];
        this.bounds = new Expression[params.size()];

        for (int i = 0; i < params.size(); i++) {
            Expression param = params.get(i);
            if (param.matches(":")) {
                this.params[i] = param.getChildren().get(0).getIdentifier();
                this.bounds[i] = param.getChildren().get(1);
            } else {
                this.params[i] = param.getIdentifier();
                this.bounds[i] = null;
            }
        }
    }

    public void accept(ArgumentList args, Environment env) {
        if (args.size() != params.length) {
            throw new RuntimeException(String.format("Function '%s' requires %d arguments but received %d",
                    name, params.length, args.size()));
        }
        for (int i = 0; i < args.size(); i++) {
            env.assignLocal(params[i], args.arg(i));
        }
    }

    public String[] getParamNames() {
        return params;
    }

}
