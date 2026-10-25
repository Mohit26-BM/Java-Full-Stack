import { Suspense, use } from "react";

type User = { name: string; email: string };

// created once, outside the component, so the Promise is stable
const userPromise: Promise<User> = new Promise(resolve =>
  setTimeout(() => resolve({ name: "Asha", email: "asha@example.com" }), 1000)
);

function UserDetails({ promise }: { promise: Promise<User> }) {
  const user = use(promise);
  return (
    <p>{user.name} — {user.email}</p>
  );
}

export default function UseApi() {
  return (
    <Suspense fallback={<p>Loading user...</p>}>
      <UserDetails promise={userPromise} />
    </Suspense>
  );
}
