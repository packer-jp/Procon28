class VectorPolygon {

    int[] xvectors;
    int[] yvectors;
    int nvectors;

    boolean matches(VectorPolygon target) {
        if (nvectors != target.nvectors) {
            return false;
        }
        check:
        for (int i = 0; i < nvectors; i++) {
            for (int j = 0; j < nvectors; j++) {
                int index = i + j < nvectors ? i + j : i + j - nvectors;
                if (xvectors[index] != target.xvectors[j] || yvectors[index] != target.yvectors[j]) {
                    continue check;
                }
            }
            return true;
        }
        return false;
    }

    PolygonEX getPolygonEX() {
        int[] xpoints = new int[nvectors];
        int[] ypoints = new int[nvectors];
        xpoints[0] = 0;
        ypoints[0] = 0;
        for (int i = 1; i < nvectors; i++) {
            xpoints[i] = xpoints[i - 1] + xvectors[i - 1];
            ypoints[i] = ypoints[i - 1] + yvectors[i - 1];
        }
        return new PolygonEX(xpoints, ypoints, nvectors);
    }

    VectorPolygon getInverse() {
        int[] xvectors = new int[nvectors];
        int[] yvectors = new int[nvectors];
        for (int i = 0; i < nvectors; i++) {
            xvectors[i] = this.xvectors[nvectors - i - 1];
            yvectors[i] = -this.yvectors[nvectors - i - 1];
        }
        return new VectorPolygon(xvectors, yvectors, nvectors, true);
    }

    VectorPolygon getRotated90() {
        int[] xvectors = new int[nvectors];
        int[] yvectors = new int[nvectors];
        for (int i = 0; i < nvectors; i++) {
            xvectors[i] = -this.yvectors[i];
            yvectors[i] = this.xvectors[i];
        }
        return new VectorPolygon(xvectors, yvectors, nvectors, true);
    }

    VectorPolygon(int[] xvectors, int[] yvectors, int nvectors, boolean isVector) {
        this.xvectors = xvectors;
        this.yvectors = yvectors;
        this.nvectors = nvectors;
    }

    VectorPolygon(int[] xpoints, int[] ypoints, int npoints) {
        xvectors = new int[npoints];
        yvectors = new int[npoints];
        for (int i = 0; i < npoints - 1; i++) {
            xvectors[i] = xpoints[i + 1] - xpoints[i];
            yvectors[i] = ypoints[i + 1] - ypoints[i];
        }
        xvectors[npoints - 1] = xpoints[0] - xpoints[npoints - 1];
        yvectors[npoints - 1] = ypoints[0] - ypoints[npoints - 1];
        nvectors = npoints;
    }
}
