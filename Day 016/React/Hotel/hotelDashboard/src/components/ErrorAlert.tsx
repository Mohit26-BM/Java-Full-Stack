function ErrorAlert({ message }: { message: string }) {
  if (!message) {
    return null;
  }

  return (
    <div className="alert alert-error" role="alert">
      {message}
    </div>
  );
}

export default ErrorAlert;
