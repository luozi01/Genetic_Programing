package EvoImage;


public enum ColorChoice implements Colors {
    RANDOM {
        @Override
        public void apply(Shape shape) {
            shape.setColor(Math.random(), Math.random(), Math.random(), Math.random());
        }
    },
    WHITE {
        @Override
        public void apply(Shape shape) {
            shape.setColor(254, 254, 254, 0.001);
        }
    },
    BLACK {
        @Override
        public void apply(Shape shape) {
            shape.setColor(0, 0, 0, 0.001);
        }
    }
}
