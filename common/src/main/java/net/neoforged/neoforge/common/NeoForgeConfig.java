package net.neoforged.neoforge.common;

public final class NeoForgeConfig {
    public static final Client CLIENT = new Client();

    private NeoForgeConfig() {
    }

    public static final class Client {
        public final BooleanValue useCombinedDepthStencilAttachment = new BooleanValue(true);
    }

    public static final class BooleanValue {
        private final boolean value;

        public BooleanValue(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }
    }
}
