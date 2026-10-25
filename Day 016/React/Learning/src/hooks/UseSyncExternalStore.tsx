import { useSyncExternalStore } from "react";

function subscribe(callback: () => void) {
  window.addEventListener("online", callback);
  window.addEventListener("offline", callback);

  return () => {
    window.removeEventListener("online", callback);
    window.removeEventListener("offline", callback);
  };
}

function getSnapshot() {
  return navigator.onLine;
}

export default function UseSyncExternalStore() {
  const isOnline = useSyncExternalStore(subscribe, getSnapshot);
  return <p>{isOnline ? "You are online" : "You are offline"}</p>;
}
