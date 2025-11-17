const win = (typeof window !== 'undefined') ? window : (typeof global !== 'undefined' ? global : this);

const blocked = () => console.log('blocked!');

(() => {
    try {
        const _origFetch = window.fetch;
        let fetchAllowed = true;

        window.fetch = function(...args) {
            if (!fetchAllowed) {
                blocked();
                return;
            }
            fetchAllowed = false;
            return _origFetch.apply(this, args);
        };
    } catch (e) {
        window.fetch = blocked;
    }

    if (win.Element && win.Element.prototype) {
        const origDesc = Object.getOwnPropertyDescriptor(win.Element.prototype, 'innerHTML');
        let innerAllowed = true;
        try {
            Object.defineProperty(win.Element.prototype, 'innerHTML', {
                configurable: true,
                enumerable: true,
                get: function() { return ''; },
                set: function(value) {
                    if (!innerAllowed) {
                        blocked();
                        return;
                    }
                    innerAllowed = false;
                    if (origDesc && typeof origDesc.set === 'function') {
                        value = DOMPurify.sanitize(value);
                        value = DOMPurify.sanitize(value);
                        return origDesc.set.call(this, value);
                    }
                }
            });
        } catch (e) {
            try {
                Object.defineProperty(win.Element.prototype, 'innerHTML', {
                    set: blocked,
                    get: function() { return ''; },
                    configurable: true
                });
            } catch (e) {}
        }
    }
})();

win.alert = blocked;
win.confirm = blocked;
win.prompt = blocked;

win.eval = blocked;
win.Function = blocked;
win.setTimeout = blocked;
win.setInterval = blocked;
win.requestAnimationFrame = blocked;
win.requestIdleCallback = blocked;
win.importScripts = blocked;

win.XMLHttpRequest = blocked;
win.WebSocket = blocked;
win.EventSource = blocked;
win.RTCPeerConnection = blocked;
win.webkitRTCPeerConnection = blocked;
win.mozRTCPeerConnection = blocked;
win.RTCDataChannel = blocked;
win.postMessage = blocked;

win.Worker = blocked;
win.SharedWorker = blocked;
win.ServiceWorker = blocked;

win.open = blocked;
win.ActiveXObject = blocked;

win.MutationObserver = blocked;
win.IntersectionObserver = blocked;
win.PerformanceObserver = blocked;
win.ResizeObserver = blocked;

win.document.write = blocked;
win.document.writeln = blocked;
win.document.open = blocked;
win.document.close = blocked;
win.document.execCommand = blocked;
win.document.createRange = blocked;

if (win.Document && win.Document.prototype) {
    win.Document.prototype.write = blocked;
    win.Document.prototype.writeln = blocked;
    win.Document.prototype.open = blocked;
    win.Document.prototype.close = blocked;
    win.Document.prototype.createElement = blocked;
    win.Document.prototype.createElementNS = blocked;
    win.Document.prototype.createTextNode = blocked;
    win.Document.prototype.createComment = blocked;
    win.Document.prototype.createDocumentFragment = blocked;
    win.Document.prototype.createCDATASection = blocked;
    win.Document.prototype.createProcessingInstruction = blocked;
    win.Document.prototype.appendChild = blocked;
    win.Document.prototype.insertBefore = blocked;
    win.Document.prototype.replaceChild = blocked;
    win.Document.prototype.importNode = blocked;
    win.Document.prototype.adoptNode = blocked;
}

if (win.Node && win.Node.prototype) {
    win.Node.prototype.appendChild = blocked;
    win.Node.prototype.insertBefore = blocked;
    win.Node.prototype.replaceChild = blocked;
    win.Node.prototype.cloneNode = blocked;
    win.Node.prototype.removeChild = blocked;
    win.Node.prototype.normalize = blocked;
}

if (win.Element && win.Element.prototype) {
    win.Element.prototype.insertAdjacentHTML = blocked;
    win.Element.prototype.insertAdjacentElement = blocked;
    win.Element.prototype.insertAdjacentText = blocked;
    win.Element.prototype.setAttribute = blocked;
    win.Element.prototype.setAttributeNS = blocked;
    win.Element.prototype.setAttributeNode = blocked;
    win.Element.prototype.setAttributeNodeNS = blocked;
    win.Element.prototype.removeAttribute = blocked;
    win.Element.prototype.removeAttributeNS = blocked;
    win.Element.prototype.removeAttributeNode = blocked;
    win.Element.prototype.remove = blocked;
    win.Element.prototype.append = blocked;
    win.Element.prototype.prepend = blocked;
    win.Element.prototype.replaceWith = blocked;
    win.Element.prototype.after = blocked;
    win.Element.prototype.before = blocked;
    win.Element.prototype.replaceChildren = blocked;
}

const eventHandlers = Object.getOwnPropertyNames(window).filter(x => x.startsWith('on')).sort();

eventHandlers.forEach(handler => {
    try {
        Object.defineProperty(win, handler, {
            set: blocked,
            get: () => null,
            configurable: true
        });
    } catch (e) {}
});

if (win.HTMLElement && win.HTMLElement.prototype) {
    eventHandlers.forEach(handler => {
        try {
            Object.defineProperty(win.HTMLElement.prototype, handler, {
                set: blocked,
                get: () => null,
                configurable: true
            });
        } catch (e) {}
    });
}

if (win.Element && win.Element.prototype) {
    eventHandlers.forEach(handler => {
        try {
            Object.defineProperty(win.Element.prototype, handler, {
                set: blocked,
                get: () => null,
                configurable: true
            });
        } catch (e) {}
    });
}

if (win.EventTarget && win.EventTarget.prototype) {
    win.EventTarget.prototype.addEventListener = blocked;
    win.EventTarget.prototype.removeEventListener = blocked;
}

if (win.location) {
    win.location.replace = blocked;
    win.location.assign = blocked;
    win.location.reload = blocked;
}

if (win.history) {
    win.history.pushState = blocked;
    win.history.replaceState = blocked;
}

try {
    Object.defineProperty(win, 'localStorage', {
        get: () => null,
        set: blocked,
        configurable: true
    });
} catch (e) {}

try {
    Object.defineProperty(win, 'sessionStorage', {
        get: () => null,
        set: blocked,
        configurable: true
    });
} catch (e) {}

try {
    Object.defineProperty(win, 'indexedDB', {
        get: () => null,
        set: blocked,
        configurable: true
    });
} catch (e) {}

try {
    Object.defineProperty(win.document, 'cookie', {
        get: () => '',
        set: blocked,
        configurable: true
    });
} catch (e) {}

try {
    Object.defineProperty(win.document, 'domain', {
        get: () => win.location.hostname,
        set: blocked,
        configurable: true
    });
} catch (e) {}

if (win.navigator && win.navigator.clipboard) {
    try {
        Object.defineProperty(win.navigator, 'clipboard', {
            get: () => null,
            set: blocked,
            configurable: true
        });
    } catch (e) {}
}

if (win.navigator && win.navigator.geolocation) {
    try {
        Object.defineProperty(win.navigator, 'geolocation', {
            get: () => null,
            set: blocked,
            configurable: true
        });
    } catch (e) {}
}

if (win.navigator && win.navigator.credentials) {
    try {
        Object.defineProperty(win.navigator, 'credentials', {
            get: () => null,
            set: blocked,
            configurable: true
        });
    } catch (e) {}
}

if (win.Notification) {
    win.Notification = blocked;
}

if (win.navigator && win.navigator.getBattery) {
    win.navigator.getBattery = blocked;
}

if (win.navigator && win.navigator.mediaDevices) {
    try {
        win.navigator.mediaDevices.getUserMedia = blocked;
        win.navigator.mediaDevices.getDisplayMedia = blocked;
        win.navigator.mediaDevices.enumerateDevices = blocked;
    } catch (e) {}
}

if (win.navigator) {
    win.navigator.getUserMedia = blocked;
    win.navigator.webkitGetUserMedia = blocked;
    win.navigator.mozGetUserMedia = blocked;
    win.navigator.sendBeacon = blocked;
}

if (win.navigator && win.navigator.bluetooth) {
    try {
        Object.defineProperty(win.navigator, 'bluetooth', {
            get: () => null,
            set: blocked,
            configurable: true
        });
    } catch (e) {}
}

if (win.navigator && win.navigator.usb) {
    try {
        Object.defineProperty(win.navigator, 'usb', {
            get: () => null,
            set: blocked,
            configurable: true
        });
    } catch (e) {}
}

if (win.PaymentRequest) {
    win.PaymentRequest = blocked;
}

if (win.Element && win.Element.prototype) {
    try {
        Object.defineProperty(win.Element.prototype, 'outerHTML', {
            set: blocked,
            get: function() { return ''; },
            configurable: true
        });
    } catch (e) {}
}

if (win.DOMParser) {
    win.DOMParser = blocked;
}

if (win.Range && win.Range.prototype) {
    win.Range.prototype.createContextualFragment = blocked;
}

if (win.XMLSerializer) {
    win.XMLSerializer = blocked;
}

if (win.URL) {
    win.URL.createObjectURL = blocked;
    win.URL.revokeObjectURL = blocked;
}

if (win.Element && win.Element.prototype) {
    try {
        win.Element.prototype.attachShadow = blocked;
    } catch (e) {}

    try {
        Object.defineProperty(win.Element.prototype, 'shadowRoot', {
            get: () => null,
            configurable: true
        });
    } catch (e) {}
}

if (win.HTMLIFrameElement && win.HTMLIFrameElement.prototype) {
    try {
        Object.defineProperty(win.HTMLIFrameElement.prototype, 'srcdoc', {
            set: blocked,
            get: () => '',
            configurable: true
        });
    } catch (e) {}

    try {
        Object.defineProperty(win.HTMLIFrameElement.prototype, 'contentWindow', {
            get: () => null,
            configurable: true
        });
    } catch (e) {}

    try {
        Object.defineProperty(win.HTMLIFrameElement.prototype, 'contentDocument', {
            get: () => null,
            configurable: true
        });
    } catch (e) {}
}

if (win.CSSStyleSheet && win.CSSStyleSheet.prototype) {
    win.CSSStyleSheet.prototype.insertRule = blocked;
    win.CSSStyleSheet.prototype.addRule = blocked;
    win.CSSStyleSheet.prototype.deleteRule = blocked;
    win.CSSStyleSheet.prototype.removeRule = blocked;
}

if (win.HTMLElement && win.HTMLElement.prototype) {
    try {
        Object.defineProperty(win.HTMLElement.prototype, 'style', {
            get: function() { return {}; },
            set: blocked,
            configurable: true
        });
    } catch (e) {}
}

if (win.HTMLFormElement && win.HTMLFormElement.prototype) {
    win.HTMLFormElement.prototype.submit = blocked;
    try {
        Object.defineProperty(win.HTMLFormElement.prototype, 'action', {
            set: blocked,
            get: function() { return ''; },
            configurable: true
        });
    } catch (e) {}
}

if (win.HTMLAnchorElement && win.HTMLAnchorElement.prototype) {
    win.HTMLAnchorElement.prototype.click = blocked;
}

if (win.HTMLScriptElement && win.HTMLScriptElement.prototype) {
    try {
        Object.defineProperty(win.HTMLScriptElement.prototype, 'src', {
            set: blocked,
            get: () => '',
            configurable: true
        });
    } catch (e) {}

    try {
        Object.defineProperty(win.HTMLScriptElement.prototype, 'text', {
            set: blocked,
            get: () => '',
            configurable: true
        });
    } catch (e) {}

    try {
        Object.defineProperty(win.HTMLScriptElement.prototype, 'textContent', {
            set: blocked,
            get: () => '',
            configurable: true
        });
    } catch (e) {}
}

if (win.HTMLObjectElement && win.HTMLObjectElement.prototype) {
    try {
        Object.defineProperty(win.HTMLObjectElement.prototype, 'data', {
            set: blocked,
            get: () => '',
            configurable: true
        });
    } catch (e) {}
}

if (win.HTMLEmbedElement && win.HTMLEmbedElement.prototype) {
    try {
        Object.defineProperty(win.HTMLEmbedElement.prototype, 'src', {
            set: blocked,
            get: () => '',
            configurable: true
        });
    } catch (e) {}
}

if (win.customElements) {
    win.customElements.define = blocked;
    win.customElements.get = blocked;
    win.customElements.whenDefined = blocked;
}

if (win.HTMLTemplateElement && win.HTMLTemplateElement.prototype) {
    try {
        Object.defineProperty(win.HTMLTemplateElement.prototype, 'content', {
            get: () => null,
            configurable: true
        });
    } catch (e) {}
}


try {
    Object.defineProperty = blocked;
} catch (e) {}

try {
    Object.setPrototypeOf = blocked;
} catch (e) {}

try {
    Object.create = blocked;
} catch (e) {}

try {
    Object.defineProperties = blocked;
} catch (e) {}

try {
    Object.setPrototypeOf = blocked;
} catch (e) {}

try {
    Object.assign = blocked;
} catch (e) {}

win.Proxy = blocked;
win.Reflect = blocked;

if (win.import) {
    win.import = blocked;
}

try {
    Object.defineProperty(Function.prototype, 'constructor', {
        get: () => blocked,
        set: blocked,
        configurable: true
    });
} catch (e) {}

try {
    if (Object.freeze) {
        Object.freeze(Object.prototype);
        Object.freeze(Function.prototype);
        Object.freeze(Array.prototype);
    }
} catch (e) {}

console.log('XSS Shield activated');

