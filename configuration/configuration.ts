export const Configuration = {
    mimicScroll: false,
    useOnScreenKeyboard: false,
    useTouchListener: true,
    useSavePoints: true,
    showRegisterStatus: false,
    clickableRegisterStatus: false,
    offlineOnlyRegisterStatus: false,
    keepAliveMillis: 30000,
    maxSignaturePoints: -1,
    maxResponseSizeBytes: 79500,
    enableAutocomplete: false,
    enableMenuClose: true,
    enableKeybinds: false,
    googleApiKey: 'no_key',

    // These properties are static on the client and not overriden by configuration.service.ts
    compatibilityVersion: 'v1',
    incompatibleVersionMessage: 'Application is not compatible with the server.',
};
