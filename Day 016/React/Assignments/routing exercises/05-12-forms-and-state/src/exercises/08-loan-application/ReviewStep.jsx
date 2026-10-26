function ReviewStep({ application, steps, onEdit }) {
  return (
    <div className="review">
      {steps.map((step, index) => (
        <section key={step.title} className="review__section" aria-label={step.title}>
          <div className="review__head">
            <h2 className="review__title">{step.title}</h2>
            <button type="button" className="button" onClick={() => onEdit(index)} aria-label={`Edit ${step.title}`}>
              Edit
            </button>
          </div>
          <dl className="review__list">
            {step.fields.map((field) => (
              <div key={field.name} className="review__row">
                <dt>{field.label}</dt>
                <dd>{application[field.name]}</dd>
              </div>
            ))}
          </dl>
        </section>
      ))}
    </div>
  )
}

export default ReviewStep
