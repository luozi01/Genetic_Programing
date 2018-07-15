package examples.EvoImage;


public enum ColorChoice implements Colors {
    RANDOM {
        @Override
        public void apply(Polygon polygon) {
            polygon.setColor(Math.random(), Math.random(), Math.random(), Math.random());
        }
    },
    WHITE {
        @Override
        public void apply(Polygon polygon) {
            polygon.setColor(1, 1, 1, 0.001);
        }
    },
    BLACK {
        @Override
        public void apply(Polygon polygon) {
            polygon.setColor(0, 0, 0, 0.001);
        }
    }
}
