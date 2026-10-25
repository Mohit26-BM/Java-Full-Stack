import { useActionState } from "react";

type FormState = { message: string };

async function signupAction(_previous: FormState, formData: FormData): Promise<FormState> {
  const email = String(formData.get("email") ?? "").trim();

  if (!email.includes("@")) {
    return { message: "Enter a valid email address." };
  }

  await new Promise(resolve => setTimeout(resolve, 500)); // fake request
  return { message: `Submitted signup for ${email}.` };
}

export default function UseActionState() {
  const [state, formAction, isPending] = useActionState(signupAction, { message: "" });

  return (
    <form action={formAction}>
      <input name="email" type="email" required />
      <button disabled={isPending}>{isPending ? "Submitting..." : "Sign up"}</button>
      <p>{state.message}</p>
    </form>
  );
}
