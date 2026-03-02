import { createRoot, type Root } from 'react-dom/client';
import React from 'react';
import { Agentation } from 'agentation';

let root: Root | null = null;

/**
 * Mount the Agentation React component into the given container.
 * Only used in development. Returns an unmount function.
 */
export function mountAgentation(container: HTMLElement): () => void {
  root = createRoot(container);
  root.render(React.createElement(Agentation, null));
  return () => {
    root?.unmount();
    root = null;
  };
}
