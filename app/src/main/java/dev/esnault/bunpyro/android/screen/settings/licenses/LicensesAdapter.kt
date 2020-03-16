package dev.esnault.bunpyro.android.screen.settings.licenses

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.esnault.bunpyro.common.openUrlInBrowser
import dev.esnault.bunpyro.databinding.ItemLicenseBinding


class LicensesAdapter(context: Context) : RecyclerView.Adapter<LicensesAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val licences = licenses

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLicenseBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isLast = position == licences.lastIndex
        holder.bind(licences[position], isLast)
    }

    override fun getItemCount(): Int = licences.size

    class ViewHolder(
        private val binding: ItemLicenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = itemView.context

        fun bind(license: License, isLast: Boolean) {
            binding.title.text = license.title
            binding.summary.text = license.summary
            binding.license.text = license.license

            binding.bottomDivider.isVisible = !isLast

            binding.root.setOnClickListener {
                context.openUrlInBrowser(license.url)
            }
        }
    }
}
